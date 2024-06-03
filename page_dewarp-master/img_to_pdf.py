from reportlab.lib.pagesizes import letter
from reportlab.pdfgen import canvas
from reportlab.lib.units import inch
from reportlab.lib import utils
import os


def add_image_to_pdf(pdf_path, image_path):
    c = canvas.Canvas(pdf_path, pagesize=letter)
    width, height = letter
    file_list = os.listdir(image_path)
    file_path = os.path.abspath(__file__).replace('img_to_pdf.py','output').replace('\\','/')
    print(file_list,file_path)
    
    for page_number in range(0, len(file_list)):
        c.saveState()
        c.drawImage(file_path + '/' + file_list[page_number], 0, 0, width=width, height=height, preserveAspectRatio=True)
        os.remove(file_path + '/' + file_list[page_number])
        c.showPage()
    
    c.save()

# PDF 파일 경로와 추가할 이미지의 경로를 지정합니다.
pdf_path = "C:/Users/user/Downloads/mdp-3-1-2-main/page_dewarp-master/output.pdf"
image_path = "C:/Users/user/Downloads/mdp-3-1-2-main/page_dewarp-master/output"

add_image_to_pdf(pdf_path, image_path)
