import cv2
import numpy as np
import simpleaudio as sa

# Load sound files
c4_wave_obj = sa.WaveObject.from_wave_file('c4.wav')
d4_wave_obj = sa.WaveObject.from_wave_file('d4.wav')
e4_wave_obj = sa.WaveObject.from_wave_file('e4.wav')
f4_wave_obj = sa.WaveObject.from_wave_file('f4.wav')
g4_wave_obj = sa.WaveObject.from_wave_file('g4.wav')
a4_wave_obj = sa.WaveObject.from_wave_file('a4.wav')
b4_wave_obj = sa.WaveObject.from_wave_file('b4.wav')

# Define the lower and upper boundaries for the colors of the piano keys
lower_boundaries = {'c': np.array([20, 100, 100]), 'd': np.array([40, 100, 100]), 
                    'e': np.array([60, 100, 100]), 'f': np.array([80, 100, 100]),
                    'g': np.array([100, 100, 100]), 'a': np.array([120, 100, 100]),
                    'b': np.array([140, 100, 100])}
upper_boundaries = {'c': np.array([30, 255, 255]), 'd': np.array([50, 255, 255]), 
                    'e': np.array([70, 255, 255]), 'f': np.array([90, 255, 255]),
                    'g': np.array([110, 255, 255]), 'a': np.array([130, 255, 255]),
                    'b': np.array([150, 255, 255])}

# Create a dictionary to map the colors of the piano keys to the corresponding sound files
sound_files = {'c': c4_wave_obj, 'd': d4_wave_obj, 'e': e4_wave_obj, 'f': f4_wave_obj,
               'g': g4_wave_obj, 'a': a4_wave_obj, 'b': b4_wave_obj}

# Initialize the camera
cap = cv2.VideoCapture(0)

# Initialize the sound player
play_obj = None

while True:
    # Capture a frame from the camera
    ret, frame = cap.read()
    
    # Convert the frame to the HSV color space
    hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
    
    # Loop through the colors of the piano keys
    for color, lower_boundary in lower_boundaries.items():
        upper_boundary = upper_boundaries[color]
        
        # Create a mask to detect the color of the piano key
        mask = cv2.inRange(hsv, lower_boundary, upper_boundary)
        
        # Find contours of the color in the mask
        contours, _ = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        
        # Check if a contour is found
        if len(contours) > 0:
            # Get the bounding box of the contour
            x, y, w, h = cv2.boundingRect(contours[0])
            
            # Draw a rectangle around the contour
            cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
            
            # Check if the height of the bounding box is greater than a threshold (to filter out noise)
            if h > 50:
                # Get the name of the piano key from the color
                note = color
                
                # Check if the sound is already playing
                if play_obj is None or play_obj.is_playing() == False:
                    # Load the sound file for the piano key
                    wave_obj = sound_files[note]
                    
                    # Play the sound file
                    play_obj = wave_obj.play()

