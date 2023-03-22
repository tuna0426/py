import cv2

# 讀取影像
image = cv2.imread('piano.jpg')

# 轉換為灰度影像
gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

# 應用閾值處理
_, thresh = cv2.threshold(gray, 150, 255, cv2.THRESH_BINARY)

# 應用輪廓檢測
contours, hierarchy = cv2.findContours(thresh, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

# 繪製輪廓
cv2.drawContours(image, contours, -1, (0, 0, 255), 3)

# 顯示結果
cv2.imshow('result', image)
cv2.waitKey(0)
cv2.destroyAllWindows()