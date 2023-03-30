import cv2
import numpy as np
import pyautogui
import keyboard
import time
import urllib.request
import pygame
import os
from pydub import AudioSegment
from pydub.playback import play
# 下載MIDI文件
url = "http://www.piano-midi.de/midifiles/mendelssohn/mendop18.mid"
urllib.request.urlretrieve(url, "music.mid")

# 將MIDI文件轉換為WAV格式
audio = AudioSegment.from_file("music.mid", format="mid")
audio.export("music.wav", format="wav")

# 播放WAV格式的背景音樂
pygame.init()
pygame.mixer.music.load("music.wav")
pygame.mixer.music.play()

while pygame.mixer.music.get_busy():
    pygame.time.Clock().tick(10)

# 設定參數
tile_width = 125    # 瓷磚寬度
tile_height = 125   # 瓷磚高度
offset_x = 625      # 遊戲區域左上角 x 座標
offset_y = 400      # 遊戲區域左上角 y 座標
gap = 5             # 瓷磚之間的間隔
keys = ['a', 's', 'd', 'f']  # 對應鍵盤按鍵

# 設定相關的按鍵和滑鼠事件


def click_tile(x, y):
    pyautogui.click(x, y)


def on_press(key):
    if key.name in keys:
        i = keys.index(key.name)
        x = offset_x + i * (tile_width + gap) + tile_width // 2
        y = offset_y + tile_height - gap
        click_tile(x, y)


# 初始化 OpenCV 相關變量
cap = cv2.VideoCapture(0)
kernel = np.ones((5, 5), np.uint8)

# 等待遊戲開始
print("請按空白鍵開始遊戲...")
keyboard.wait('space')

# 設置鍵盤監聽
keyboard.on_press(on_press)

# 開始遊戲循環
while True:
    # 檢測遊戲是否結束
    if keyboard.is_pressed('esc'):
        break

    # 擷取畫面
    ret, frame = cap.read()

    # 將畫面轉為灰度圖像
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    # 對圖像進行二值化處理
    ret, binary = cv2.threshold(gray, 150, 255, cv2.THRESH_BINARY)

    # 對二值化圖像進行膨脹操作，以填充瓷磚
    dilated = cv2.dilate(binary, kernel, iterations=3)

    # 檢測瓷磚是否出現
    for i, key in enumerate(keys):
        x = offset_x + i * (tile_width + gap) + tile_width // 2
        y = offset_y + tile_height - gap
        if dilated[y, x] == 255:
            click_tile(x, y)
            break

    # 延時一段時間，讓遊戲進行
    time.sleep(0.01)

print("遊戲結束")

# 釋放相關資源
cap.release()
cv2.destroyAllWindows()
音樂部分程式碼參考
