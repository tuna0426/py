import cv2
import numpy as np

# 定義初始位置和大小
x, y = 100, 100
w, h = 50, 50

# 創建一個黑色畫布
canvas = 255 * np.ones((480, 640, 3), dtype=np.uint8)

# 創建一個窗口
cv2.namedWindow('Square')

# 移動方形
while True:
    # 繪製方形
    cv2.rectangle(canvas, (x, y), (x + w, y + h), (0, 0, 255), -1)

    # 在窗口中顯示方形
    cv2.imshow('Square', canvas)

    # 等待一段時間
    cv2.waitKey(50)

    # 將方形向右移動
    x += 5

    # 如果方形移出窗口，重新從左側開始移動
    if x >= canvas.shape[1]:
        x = 0 - w

    # 如果按下 ESC 鍵，退出程式
    if cv2.waitKey(1) & 0xFF == 27:
        break

# 釋放資源
cv2.destroyAllWindows()
