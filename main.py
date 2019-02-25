# This kivy app is the client software for the Galaxy S3 mini with 
# the Wifi camera script running. When in the same LAN,
# it will request a picture from the Wifi camera and present it
# in the UI. 

from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.properties import ObjectProperty
import socket


HOST = '192.168.178.22' #change this to the IP of the Wifi camera
PORT = 12345


def receive_image_tcp(filename, host, port):
    #create socket with timeout
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.settimeout(90)

    #try to connect to socket
    try:
        s.connect((host, port))
    except socket.timeout:
        print('Connection timed out')
        return False
    except ConnectionRefusedError:
        print('Connection refused')
        return False
    except OSError as err:
        if hasattr(err, 'strerror'):
            print(err.strerror)
        print('SmartCam not available')
        return False

    # send command for sending new picture
    try:
        s.send(bytes('SEND_IMG', 'utf8'))
    except OSError:
        print('Sending command failed')
        return False
    s.shutdown(socket.SHUT_WR)

    # open a file to write the new picture to
    imgfile = open(filename, 'wb')

    try:
        #start receiving from socket
        buffer = s.recv(1024)
        while buffer:
            print('Receiving ...')
            imgfile.write(buffer)
            buffer = s.recv(1024)
    except OSError:
        print('Receiving interrupted')
        imgfile.close()
        return False

    # when the file has been received, close file and socket
    imgfile.close()
    s.close()

    return True


class RootWidget(BoxLayout):
    img = ObjectProperty(None)
    btn = ObjectProperty(None)

    def btn_pressed(self):
        if receive_image_tcp('image.png', HOST, PORT):
            self.img.source = 'image.png'
            self.img.reload()


class SmartCamApp(App):
    def build(self):
        wi = RootWidget(orientation='vertical')
        return wi


if __name__ == '__main__':
    SmartCamApp().run()
