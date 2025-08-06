const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.generatePasswordResetLink = functions.https.onRequest(async (req, res) => {
  // Use POST only
  if (req.method !== "POST") {
    return res.status(405).send({ error: "Method Not Allowed" });
  }

  const email = req.body.email;
  if (!email) {
    return res.status(400).send({ error: "Email is required" });
  }

  try {
    // Get the user record
    const userRecord = await admin.auth().getUserByEmail(email);

    // Check if user has email/password provider
    const hasPasswordProvider = userRecord.providerData.some(
      (provider) => provider.providerId === "password"
    );

    if (!hasPasswordProvider) {
      return res.status(403).send({ error: "User did not sign up with email/password. Password reset is not available for this account." });
    }

    const actionCodeSettings = {
      url: "https://edifyhub-e0793.web.app/finishReset",
      handleCodeInApp: true,
    };

    const link = await admin.auth().generatePasswordResetLink(email, actionCodeSettings);
    res.send({ link });
  } catch (error) {
    console.error("Error generating reset link:", error);
    if (error.code === "auth/user-not-found") {
      res.status(404).send({ error: "No user found with this email." });
    } else {
      res.status(500).send({ error: error.message });
    }
  }
});