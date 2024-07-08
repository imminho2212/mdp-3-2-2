from reportlab.pdfgen import canvas
import cv2
import os

def add_image_to_pdf(pdf_path, image_path):
    c = canvas.Canvas(pdf_path, pagesize=(1800,2540))
    width, height = (1800,2540)
    file_list = os.listdir(image_path)
    file_path = os.path.abspath(__file__).replace('img_to_pdf.py','output').replace('\\','/')
    print(file_list,file_path)
    
    for page_number in range(0, len(file_list)):
        img = cv2.imread(file_path + '/' + file_list[page_number])
        img = cv2.resize(img,(1800,2540))
        cv2.imwrite(file_path + '/' + file_list[page_number],img)
        c.saveState()
        c.drawImage(file_path + '/' + file_list[page_number], 0, 0, width=width, height=height, preserveAspectRatio=True)
        c.showPage()
    
    c.save()

# PDF 파일 경로와 추가할 이미지의 경로를 지정합니다.
pdf_path = "/home/pi/mdp/page_dewarp-master/output.pdf"
image_path = "/home/pi/mdp/page_dewarp-master/output"

add_image_to_pdf(pdf_path, image_path)
