from reportlab.lib.pagesizes import letter
from reportlab.pdfgen import canvas
from reportlab.lib.units import inch
from reportlab.lib import utils
from reportlab.lib.utils import ImageReader
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
import os
import easyocr
import cv2
import matplotlib.pyplot as plt
import PIL
from PIL import ImageFont, ImageDraw, Image

pdfmetrics.registerFont(TTFont("나눔고딕", "/home/pi/mdp/page_dewarp-master/NanumGothic-Bold.ttf"))

# PDF 파일 경로와 추가할 이미지의 경로를 지정합니다.
pdf_path = "/home/pi/mdp/page_dewarp-master/output_ocr.pdf"
image_path = "/home/pi/mdp/page_dewarp-master/output"
reader = easyocr.Reader(['ko', 'en'], gpu = False)

def add_image_to_pdf(pdf_path, image_path):
    c = canvas.Canvas(pdf_path, pagesize=(900,1275))
    width, height = (900,1275)
    file_list = os.listdir(image_path)
    file_path = os.path.abspath(__file__).replace('ttt.py','output').replace('\\','/')
    print(file_list,file_path)
    
    for page_number in range(0, len(file_list)):
        currentimgname = file_path + '/' + file_list[page_number]
        savename = file_path.replace('output','qwe') + '/' + file_list[page_number]
        img = cv2.imread(currentimgname)
        img = cv2.resize(img,(900,1275))
        cv2.imwrite(savename,img)
        result = reader.readtext(img)
        pil_img = Image.open(savename)
        draw = ImageDraw.Draw(pil_img)
        for i in result:
            draw.rectangle([(i[0][0][0],i[0][0][1]),(i[0][1][0],i[0][2][1])],fill="#ffffff",width=3)
        pil_img.save(savename)
        img2 = ImageReader(savename)
        os.remove(savename)
        c.saveState()
        c.drawImage(img2, 0, 0, width=width, height=height, preserveAspectRatio=True)
        for i in result:
            x = i[0][0][0] 
            y = i[0][0][1] 
            w = i[0][1][0] - i[0][0][0] 
            h = i[0][2][1] - i[0][1][1]
            c.setFont("나눔고딕",h/1.6)
            c.drawString(x,1275-(y+h/2),i[1])
        c.showPage()
    
    c.save()

add_image_to_pdf(pdf_path, image_path)
