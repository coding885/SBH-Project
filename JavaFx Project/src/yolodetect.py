import cv2
import torch
import base64
import sys
from ultralytics import YOLO

# Load YOLOv8 model
model = YOLO("yolo12n.pt")  # Change to your custom YOLOv8 model for mobile detection

# Open video feed (0 for webcam, or replace with video file path)
cap = cv2.VideoCapture(0)

while cap.isOpened():
    ret, frame = cap.read()
    if not ret:
        break  # Exit if the video ends or there's an issue

    # Run YOLO detection
    results = model(frame)

    # Count detected mobile phones
    mobile_count = 0
    for result in results:
        for box in result.boxes:
            cls = int(box.cls[0])  # Class index
            class_name = model.names[cls]  # Get class name

            if class_name.lower() in ["mobile phone", "cell phone", "phone"]:  # Adjust based on dataset labels
                mobile_count += 1

    # Print ONLY the count (Java reads this output)
    print(mobile_count)
    sys.stdout.flush()  # Ensure output is immediately available to Java

    # Show the video feed with detections (optional)

    # Encode the frame with annotations as base64 and output
    _, buffer = cv2.imencode('.jpg', frame)
    base64_frame = base64.b64encode(buffer).decode('utf-8')
    print(base64_frame)
    sys.stdout.flush()

    # Press 'q' to exit
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# Release resources
cap.release()
cv2.destroyAllWindows()
