import cv2
import serial
import os


port = '/dev/ttyAMA0'
baud = 115200
count = 1

ser = serial.Serial(port, baud, timeout=0.5)

cap = cv2.VideoCapture(0)     

cap.set(cv2.CAP_PROP_FRAME_WIDTH, 1020)
cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 720)

if cap.isOpened():                    
    while True: 
        received_data = ser.read()
        data_left = ser.inWaiting()           
        received_data += ser.read(data_left)
        print(received_data)
        ret, img = cap.read()           
        if ret:                        
            showimg = cv2.line(img,(510,0),(510,720),(0,0,255))
            leftimage = img[0:720,0:509].copy()
            rightimage = img[0:720,511:1020].copy()
            cv2.imshow('', showimg) 
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
        else:                          
            break
        
else:
    print("Can't open video.")         
cap.release()                         
cv2.destroyAllWindows()
