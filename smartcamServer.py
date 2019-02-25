# This is a script to be used with qPython3 on a Galaxy S3 mini
# The script turns the phone into a Wifi camera. 
# When receiving a TCP command, it will turn on the flash,
# take a picture and send the picture back to the client
# via TCP. 

import socket
import androidhelper as android
from os import system

# Change file permissions for flash file using su
system('su -c chmod 777 /sys/class/camera/flash/rear_flash')

HOST = ''
PORT = 12345
BUFSIZ = 1024
droid = android.Android()
picturePath = 'storage/sdcard0/DCIM/Camera/tmpImage.jpg'

# Define function that is taking that pictures and turns on / off the flash
def take_picture(flash):
    with open('/sys/class/camera/flash/rear_flash', 'w') as FLASH:
        if flash:
            FLASH.write('1')
        else:
            FLASH.write('0')
            
    droid.cameraCapturePicture(picturePath)
    with open('/sys/class/camera/flash/rear_flash', 'w') as FLASH:
        FLASH.write ('0')
        
    return True


# Start a simple TCP server
SERVER = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
SERVER.bind((HOST, PORT))

SERVER.listen(5)
print('Waiting for connection...')

# Start endless loop to wait for clients and send pictures
while True:
    (client, address) = SERVER.accept()

    print('Client connected.')

    received_msg = client.recv(BUFSIZ).decode('utf8')
    print('Server received: ' + received_msg)

    if received_msg == 'SEND_IMG':
        
        take_picture (True)
        
        img = open(picturePath, 'rb')
        
        try:
            buffer = img.read(BUFSIZ)
        
            while buffer:
                print ('Sending...')
                client.send(buffer)
                buffer = img.read (BUFSIZ)
        except socket.error:
            print ('Connection broken')
            img.close()
            continue
        
        img.close()
        client.shutdown(socket.SHUT_WR)
        print('Sending finished')
        
        client.close()
        
    else:
        client.close()
        

    
