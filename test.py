import cv2

# 導入鋼琴樂譜圖片
img = cv2.imread("sheet_music.png")

# 將圖像轉換為灰度圖像
gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
# 二值化處理
_, thresh = cv2.threshold(
    gray, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)
# 找出輪廓
contours, _ = cv2.findContours(thresh, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

# 將輪廓存儲為列表
key_contours = []
for contour in contours:
    area = cv2.contourArea(contour)
    if area > 50:
        key_contours.append(contour)
# 定義函數來識別黑鍵和白鍵


def is_black_key(contour):
    x, y, w, h = cv2.boundingRect(contour)
    aspect_ratio = float(w) / h
    if aspect_ratio < 0.5:
        return True
    else:
        return False


# 分類黑鍵和白鍵
black_keys = []
white_keys = []
for contour in key_contours:
    if is_black_key(contour):
        black_keys.append(contour)
    else:
        white_keys.append(contour)

# 存儲鍵的位置信息
key_positions = {}
for key in black_keys:
    x, y, w, h = cv2.boundingRect(key)
    key_positions[x] = "black"
for key in white_keys:
    x, y, w, h = cv2.boundingRect(key)
    key_positions[x] = "white"
# 定義函數來識別黑鍵和白鍵


def is_black_key(contour):
    x, y, w, h = cv2.boundingRect(contour)
    aspect_ratio = float(w) / h
    if aspect_ratio < 0.5:
        return True
    else:
        return False


# 分類黑鍵和白鍵
black_keys = []
white_keys = []
for contour in key_contours:
    if is_black_key(contour):
        black_keys.append(contour)
    else:
        white_keys.append(contour)

# 存儲鍵的位置信息
key_positions = {}
for key in black_keys:
    x, y, w, h = cv2.boundingRect(key)
    key_positions[x] = "black"
for key in white_keys:
    x, y, w, h = cv2.boundingRect(key)
    key_positions[x] = "white"
# 讀取鏡頭
cap = cv2.VideoCapture(0)

while True:
    # 抓取鏡頭圖像
    ret, frame = cap.read()

    # 將圖像轉換為灰度圖像
    gray = cv2
