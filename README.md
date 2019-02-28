# smartCam

I guess, most of you probably have an old smartphone lying around somewhere. So did I (an old Samsung Galaxy S3 mini) and since it was actually just old, but still working, I was thinking how to give it a new purpose. Then, my Dad told me about how nasty it is to always go in the cellar of our house to check if our old heating system is working properly. He asked me if there is some inexpensive way to install a camera there to check it from upstairs. So, what has a camera, a light and an integrated Wifi functionality? Exactly. 

So here is my solution for the topic based on the S3 mini. For other Android smartphones you will probably need to change the code if you want to use the flash light. 

# Installation

The old phone is turned into a TCP Server using a Python script which is run on the phone using the qPython3 app. It then waits for a signal over the Wifi to take a picture and send it back to the client. In my code, the flash is always used (cellar ...), but this can be changed as needed. You will, however, need a rooted phone for the flash functionality. On your Wifi router, set the smartCam phone to a static IP, if you don't want to keep changing the IP in the client script.

The client software is also a Python app using Kivy to generate a user interface. So you will need to install Kivy before you can use the app as is. Also, in the script of the client, you will need to change the IP of the camera to the one you assigned to it in your Wifi router.

# Ideas and plans

- Originally, I was using kivy since I was trying around with Python on Android at that time. Meanwhile I gave up on this, and so I was thinking to write a proper Android app for the client software in Kotlin. Let's see when I get to work on that one...

- Also the server script could be changed to an actual Android app, but since the setup with qPython3 is quite straightforward and the script is now working since almost half a year without trouble, I am not planning to do that too soon.
