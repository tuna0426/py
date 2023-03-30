# video tutorial https://youtu.be/yt20FGrZmbM

from PIL import ImageGrab
import cv2
import numpy as np
from pynput.keyboard import Controller

flaga = True
flagb = True
flagc = True
flagd = True

mouse = Controller()

while True:
    blank = np.zeros((200, 200, 3))

    # 8,40,560,840 depends on yopu system it may change
    captured = ImageGrab.grab(bbox=(8, 40, 560, 840))
    convt = np.array(captured)

    gray = cv2.cvtColor(convt, cv2.COLOR_BGR2GRAY)
    _, threshd = cv2.threshold(gray, 20, 255, cv2.THRESH_BINARY)

# 600,70 also depends on your system similarly alm below
    if threshd[600, 70] == 0:
        if flaga:
            cv2.putText(blank, "a", (100, 100),
                        cv2.FONT_HERSHEY_SIMPLEX, 2, (0, 255, 0), 3)

            # i have used VOX EMULATOR TO PLAY GAME and assigned key at particular posiition
            mouse.press('a')
            mouse.release('a')
            flaga = False
            flagb = True
            flagc = True
            flagd = True

    elif threshd[600, 200] == 0:
        if flagb:
            cv2.putText(blank, "b", (100, 100),
                        cv2.FONT_HERSHEY_SIMPLEX, 2, (0, 255, 0), 3)

            mouse.press('b')
            mouse.release('b')
            flaga = True
            flagb = False
            flagc = True
            flagd = True

    elif threshd[600, 350] == 0:
        if flagc:
            cv2.putText(blank, "c", (100, 100),
                        cv2.FONT_HERSHEY_SIMPLEX, 2, (0, 255, 0), 3)

            mouse.press('c')
            mouse.release('c')
            flaga = True
            flagb = True
            flagc = False
            flagd = True

    elif threshd[600, 500] == 0:
        if flagd:
            cv2.putText(blank, "d", (100, 100),
                        cv2.FONT_HERSHEY_SIMPLEX, 2, (0, 255, 0), 3)

            mouse.press('d')
            mouse.release('d')
            flaga = True
            flagb = True
            flagc = True

            flagd = False

    cv2.imshow("thresh", threshd)
    cv2.imshow("res", blank)

    if cv2.waitKey(1) == ord('q'):
        break
