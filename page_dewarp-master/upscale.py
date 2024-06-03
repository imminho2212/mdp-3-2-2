import os
import time
from threading import Thread

def a1():
    os.system('python C:/Users/user/Downloads/mdp-3-1-2-main/page_dewarp-master/take.py')

def a2():
    while True:
        if(len(os.listdir("./input"))>=10):
            os.system('python C:/Users/user/Downloads/mdp-3-1-2-main/page_dewarp-master/page_dewarp.py')

p1 = Thread(target=a1,args=())
p2 = Thread(target=a2,args=())

p1.start()
p2.start()

p1.join()

os.system('python C:/Users/user/Downloads/mdp-3-1-2-main/page_dewarp-master/img_to_pdf.py')
