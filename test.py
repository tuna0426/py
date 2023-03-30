import cv2

# 讀取圖片
img = cv2.imread("piano.png")
img = cv2.resize(img, (1200, 600))
# 將圖片轉為灰階
gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

# 二值化圖片
ret, thresh = cv2.threshold(gray, 127, 255, cv2.THRESH_BINARY_INV)

# 尋找輪廓
contours, hierarchy = cv2.findContours(
    thresh, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

# 繪製輪廓
cv2.drawContours(img, contours, -1, (0, 0, 255), 3)

# 顯示圖片
cv2.imshow("Image", img)
cv2.waitKey(0)
cv2.destroyAllWindows()
