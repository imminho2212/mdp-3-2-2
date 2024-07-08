import cv2
import os
from picamera2 import Picamera2
count = 1

picam2 = Picamera2()
picam2.configure(picam2.create_preview_configuration(main={"format": 'XRGB8888',"size":(3600,2540)}))
picam2.start()
picam2.set_controls({"LensPosition" : 5})
                 
while True: 
    img = picam2.capture_array()
    img = img[340:2540,200:3400]
    showimg = cv2.line(img,(1790,0),(1810,2200),(0,0,255))
    leftimage = img[0:2200,0:1800].copy()
    rightimage = img[0:2200,1800:3600].copy()
    cv2.imshow('', cv2.resize(showimg,dsize=(1000,800)))
    key = cv2.waitKey(25)
    if 'end.txt' in os.listdir("./qwe"):
        break           
    if key == 27:
        os.system('touch /home/pi/mdp/page_dewarp-master/qwe/end.txt')
        break
    if key == 13:
        cv2.imwrite('/home/pi/mdp/page_dewarp-master/input/image{0}.jpg'.format(count), leftimage )
        cv2.imwrite('/home/pi/mdp/page_dewarp-master/input/image{0}.jpg'.format(count+1),rightimage)
        count += 2
    if 'take.txt' in os.listdir("./command"):
        os.remove('/home/pi/mdp/page_dewarp-master/command/take.txt')
        cv2.imwrite('/home/pi/mdp/page_dewarp-master/input/image{0}.jpg'.format(count), leftimage )
        cv2.imwrite('/home/pi/mdp/page_dewarp-master/input/image{0}.jpg'.format(count+1),rightimage)
        count += 2
                            
cv2.destroyAllWindows()
