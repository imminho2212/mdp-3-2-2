import cv2
import os
# import multiprocessing

# def pagedewarpAndTopdf():
    # os.system("python C:/Users/user/Downloads/mdp-3-1-2-main/page_dewarp-master/page_dewarp.py")
    # os.system("python C:/Users/user/Downloads/mdp-3-1-2-main/page_dewarp-master/img_to_pdf.py")

count = 1

cap = cv2.VideoCapture(0)     

cap.set(cv2.CAP_PROP_FRAME_WIDTH, 1280)
cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 640)

if cap.isOpened():                    
    while True: 
        ret, img = cap.read()           
        if ret:                        
            showimg = cv2.line(img,(640,0),(640,640),(0,0,255))
            leftimage = img[0:640,0:639].copy()
            rightimage = img[0:640,641:1280].copy()
            cv2.imshow('a', showimg)
            rightimage = cv2.resize(rightimage,dsize=(0,0),fx=2,fy=2) 
            leftimage = cv2.resize(leftimage,dsize=(0,0),fx=2,fy=2) 
            key = cv2.waitKey(25)           
            if key == 27:
                break
            if key == 13:
                cv2.imwrite('C:/Users/user/Downloads/mdp-3-1-2-main/page_dewarp-master/input/image{0}.jpg'.format(count), leftimage )
                cv2.imwrite('C:/Users/user/Downloads/mdp-3-1-2-main/page_dewarp-master/input/image{0}.jpg'.format(count+1),rightimage)
                count += 2
        else:                          
            break
        
else:
    print("Can't open video.")         
cap.release()                         
cv2.destroyAllWindows()

