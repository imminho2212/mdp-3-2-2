import os
from threading import Thread

def a1():
    os.system('python /home/pi/mdp/page_dewarp-master/take.py')

def a2():
    while True:
        if(len(os.listdir("./input"))>=4):
            os.system('python /home/pi/mdp/page_dewarp-master/page_dewarp.py')
        if 'end.txt' in os.listdir("./qwe"):
            if not None in os.listdir("/home/pi/mdp/page_dewarp-master/input"):
                os.system('python /home/pi/mdp/page_dewarp-master/page_dewarp.py')
            break

def a3():
    os.system('python /home/pi/mdp/page_dewarp-master/serial_aa.py')

def a4():
    os.system('python /home/pi/mdp/page_dewarp-master/server.py')

p1 = Thread(target=a1,args=())
p2 = Thread(target=a2,args=())
p3 = Thread(target=a3,args=())
p4 = Thread(target=a4,args=())

p1.start()
p2.start()
p3.start()
p4.start()

p1.join()
p3.join()
p2.join()
os.system('curl http://localhost:5000/shutdown')
p4.join()

os.remove("/home/pi/mdp/page_dewarp-master/qwe/end.txt")
os.system('python /home/pi/mdp/page_dewarp-master/img_to_pdf.py')
os.system('python /home/pi/mdp/page_dewarp-master/img_to_pdf_text_gco.py')
