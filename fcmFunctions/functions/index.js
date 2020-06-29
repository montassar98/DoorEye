const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.pushNotification = functions.database.ref('/BoxList/{BoxId}/Calling/').onWrite( ( change,context) => {
console.log('Push notification event triggered');

    const message = change.after.val();
    console.log(message);

    const topic = "mTopic";
    const payload = {
        notification: { 
				'title': "Notification from Functions",
				'body':   JSON.stringify(message)
        },
        data: { 
          'title': "data from Functions",
          'body':   JSON.stringify(message)
          }
         
    };

    return admin.messaging().sendToTopic(topic, payload);
   });

  