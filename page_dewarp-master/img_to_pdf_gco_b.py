from reportlab.pdfgen import canvas
from reportlab.lib.utils import ImageReader
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
import os
import io
import cv2
import extcolors
from google.cloud import vision
from PIL import ImageDraw, Image

os.environ['GOOGLE_APPLICATION_CREDENTIALS']=r"/home/pi/mdp/page_dewarp-master/ocrtest-427404-b5415e2a8b68.json"
client_options = {'api_endpoint': 'eu-vision.googleapis.com'}
client = vision.ImageAnnotatorClient(client_options=client_options)
pdfmetrics.registerFont(TTFont("나눔고딕", "/home/pi/mdp/page_dewarp-master/NanumGothic-Bold.ttf"))

# PDF 파일 경로와 추가할 이미지의 경로를 지정합니다.
pdf_path = "/home/pi/mdp/page_dewarp-master/output_ocr.pdf"
image_path = "/home/pi/mdp/page_dewarp-master/output"

def add_image_to_pdf(pdf_path, image_path):
    c = canvas.Canvas(pdf_path, pagesize=(1800,2540))
    width, height = (1800,2540)
    file_list = os.listdir(image_path)
    file_path = os.path.abspath(__file__).replace('img_to_pdf_text_gco.py','output').replace('\\','/')
    print(file_list,file_path)
    
    for page_number in range(0, len(file_list)):
        currentimgname = file_path + '/' + file_list[page_number]
        savename = file_path.replace('output','qwe') + '/' + file_list[page_number]
        img = cv2.imread(currentimgname)
        img = cv2.resize(img,(1800,2540))
        cv2.imwrite(savename,img)

        content = io.open(savename, 'rb').read()
        image = vision.Image(content=content)
        response = client.text_detection(image=image)
        texts = response.text_annotations
        texts = texts[1:]

        pil_img = Image.open(savename)
        draw = ImageDraw.Draw(pil_img)

        for text in texts:
        
            vertices = (['({},{})'.format(vertex.x, vertex.y)
                        for vertex in text.bounding_poly.vertices])
            
            ocr_text = text.description
            x1 = text.bounding_poly.vertices[0].x
            y1 = text.bounding_poly.vertices[0].y
            x2 = text.bounding_poly.vertices[1].x
            y2 = text.bounding_poly.vertices[2].y
            if(x2<x1): x2 = x1
            if(y2<y1): y2 = y1
            colors, pixel_count = extcolors.extract_from_image(pil_img.crop((x1,y1,x2,y2)))
            try:
                color = str(format(colors[0][0][0],'#x')[2:])+str(format(colors[0][0][1],'#x')[2:])+str(format(colors[0][0][2],'#x')[2:])
            except:
                pass
            draw.rectangle([(x1-3,y1-3),(x2+3,y2+3)],fill="#{}".format(color),width=5)
        pil_img.save(savename)
        img2 = ImageReader(savename)
        os.remove(savename)
        c.saveState()
        c.drawImage(img2, 0, 0, width=width, height=height, preserveAspectRatio=True)
        for text in texts:
        
            vertices = (['({},{})'.format(vertex.x, vertex.y)
                        for vertex in text.bounding_poly.vertices])
            
            ocr_text = text.description
            x1 = text.bounding_poly.vertices[0].x
            y1 = text.bounding_poly.vertices[0].y
            x2 = text.bounding_poly.vertices[1].x
            y2 = text.bounding_poly.vertices[2].y

            h = y2 - y1

            c.setFont("나눔고딕",h/1.3)
            c.drawString(x1,2540-(y1+h/1.5),ocr_text)
            
        c.showPage()
    
    c.save()

add_image_to_pdf(pdf_path, image_path)
