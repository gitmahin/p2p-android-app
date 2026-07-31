# P2P Messaging App

An offline peer-to-peer messaging application for Android using Android Wi-Fi Direct (WifiP2pManager). The app enables nearby devices to discover each other and exchange messages without requiring an internet connection, making it suitable for communication in areas with limited or no network coverage.

## Download The App: [CrisisTech APK](https://www.dropbox.com/scl/fi/9kx1k6vtpct1ancyr1a4d/crisis-tech-latest.apk?rlkey=3n42ksypgmmip8qj83clnhr5g&st=puo2ssqu&dl=1)

---

## *What I planned beyond the current version of my P2P Wi-Fi Direct Messaging App (for no-internet situations):*

This project was built in just 2 days, so I couldn't implement everything I had planned. Here are some of the features I wanted to add:

### User Profiles
- Save the user's name and contact information in local phone storage.
- Exchange profile information automatically when users connect, so chats display real names instead of only device information.

### Offline AI Incident Detection (TensorFlow Lite)
- Integrate a local TensorFlow Lite model trained using MobileNet/ImageNet with a custom incident dataset.
- When a user uploads an incident photo, the model would classify it as High, Medium, Low, or Safe risk.

#### The app would then save and share:
- Incident location
- Risk label
- Incident summary (provided by the user)

### Smart P2P Data Synchronization Since a Wi-Fi Direct group has one host and multiple clients, the synchronization logic was designed as follows:
- If the host creates a message or incident report, it broadcasts the data to all connected clients.
- If a client creates a report, it first sends the data to the host.
- The host then redistributes that data to every connected client, ensuring everyone stays synchronized.
- To prevent duplicate messages or reports during synchronization, every piece of data would be assigned a UUID, allowing devices to identify and ignore already-synced content.

### Multi-Group Networking: 
Another feature I planned was overcoming the limitations of Wi-Fi Direct. Since Wi-Fi Direct groups are isolated and cannot communicate with other groups directly, I planned to implement a bridge-node architecture. In this design, two devices from different Wi-Fi Direct groups would connect to each other over Bluetooth and relay data between the groups. This would effectively chain multiple Wi-Fi Direct groups together, creating a larger mesh-like offline network instead of being limited to a single group.

There are many tricky edge cases and networking challenges involved in making this work reliably. Unfortunately, I couldn't complete all of these features because the entire project had to be built within just two days.

Even so, I'm happy with what I managed to accomplish. 😊😊
