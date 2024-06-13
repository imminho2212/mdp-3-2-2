import os
import cv2
import serial
from picamera2 import Picamera2

port = '/dev/ttyAMA0'
baud = 115200
count = 1

ser = serial.Serial(port, baud, timeout=0.1)

picam2 = Picamera2()
picam2.configure(picam2.create_preview_configuration(main={"format": 'XRGB8888',"size":(3600,2540)}))
picam2.start()
picam2.set_controls({"LensPosition" : 5})
                 
while True: 
    received_data = ser.read()
    data_left = ser.inWaiting()           
    received_data += ser.read(data_left)
    print(received_data)
    ret = picam2.capture_array()
    img = picam2.capture_array()
    cv2.imshow('', ret)
    received_data = ser.read()
    data_left = ser.inWaiting()           
    received_data += ser.read(data_left) 
    showimg = cv2.line(img,(1810,0),(1790,2540),(0,0,255))
    leftimage = img[0:2540,0:1800].copy()
    rightimage = img[0:2540,1800:3600].copy()
    cv2.imshow('', cv2.resize(showimg,dsize=(640,480)))
    received_data = ser.read()
    data_left = ser.inWaiting()           
    received_data += ser.read(data_left) 
    key = cv2.waitKey(25)           
    if key == 27:
        break
    if key == 13:
        cv2.imwrite('/home/pi/mdp/page_dewarp-master/input/image{0}.jpg'.format(count), leftimage )
        cv2.imwrite('/home/pi/mdp/page_dewarp-master/input/image{0}.jpg'.format(count+1),rightimage)
        count += 2
    if received_data == b'start':
        cv2.imwrite('/home/pi/mdp/page_dewarp-master/input/image{0}.jpg'.format(count), leftimage )
        cv2.imwrite('/home/pi/mdp/page_dewarp-master/input/image{0}.jpg'.format(count+1),rightimage)
        count += 2
        ser.write(b'next\n')
                            
cv2.destroyAllWindows()
