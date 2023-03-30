import cv2

# 鋼琴黑鍵和白鍵的寬度比例
BLACK_WHITE_RATIO = 0.6

# 音符名稱列表
NOTE_NAMES = ["C", "D", "E", "F", "G", "A", "B"]

# 讀取圖片
img = cv2.imread("piano.png")

# 將圖片轉為灰階
gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

# 二值化圖片
ret, thresh = cv2.threshold(gray, 127, 255, cv2.THRESH_BINARY_INV)

# 尋找輪廓
contours, hierarchy = cv2.findContours(
    thresh, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

# 計算黑白鍵的寬度
keys = []
for cnt in contours:
    x, y, w, h = cv2.boundingRect(cnt)
    if w < 10 or h < 10:
        continue
    if float(w) / h < BLACK_WHITE_RATIO:
        # 黑鍵
        keys.append((x, y, w, h, "black"))
    else:
        # 白鍵
        keys.append((x, y, w, h, "white"))

# 以鍵位的x座標為基礎，排序所有鍵位
keys = sorted(keys, key=lambda k: k[0])

# 計算每個鍵位對應的音符名稱
notes = []
for i, key in enumerate(keys):
    if key[4] == "white":
        note_idx = i // 7
        note_name = NOTE_NAMES[note_idx]
        octave = (i // 7) + 1
        notes.append((key[0], key[1], key[2], key[3], note_name + str(octave)))
    else:
        notes.append((key[0], key[1], key[2], key[3], ""))

# 繪製鍵位和音符名稱
for note in notes:
    x, y, w, h, note_name = note
    cv2.rectangle(img, (x, y), (x + w, y + h), (0, 255, 0), 2)
    if note_name:
        cv2.putText(img, note_name, (x, y - 10),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)

# 顯示圖片
cv2.imshow("Image", img)
cv2.waitKey(0)
cv2.destroyAllWindows()
