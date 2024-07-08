import serial
import os

port = '/dev/ttyAMA0'
baud = 115200

ser = serial.Serial(port, baud, timeout=0.2)
while True:
    received_data = ser.read()
    data_left = ser.inWaiting()           
    received_data += ser.read(data_left)
    print(received_data)

    if(received_data==b"pagenum\n\x00"):
        pagenum = os.listdir("./pagenum")[0].replace('.txt','')+'\n'
        ser.write(f"{pagenum:04d}".encode("utf-8"))

    if(received_data==b"start\n\x00"):
        os.system("touch /home/pi/mdp/page_dewarp-master/command/take.txt")
        ser.write(b'next\n')

    if(received_data==b"stop\n\x00"):
        os.system("rm /home/pi/mdp/page_dewarp-master/command/take.txt")

    if(received_data==b"end\n\x00"):
        os.system("touch /home/pi/mdp/page_dewarp-master/qwe/end.txt")

    if 'end.txt' in os.listdir("/home/pi/mdp/page_dewarp-master/qwe"):
        break
