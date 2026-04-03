import * as ftp from 'basic-ftp';

async function testConnection() {
    const client = new ftp.Client();
    client.ftp.verbose = true;
    try {
        console.log("--- Testing FTP Connection to sarkariexamai.com ---");
        await client.access({
            host: "103.86.176.249",
            user: "253@sarkariexamai.com",
            password: "SarkariExam99).",
            secure: "explicit"
        });
        console.log("✅ SUCCESS: Logged in successfully!");
    } catch (err) {
        console.log("❌ FAILED: " + err.message);
        console.log("Trying alternative host: ftp.sarkariexamai.com...");
        try {
            await client.access({
                host: "ftp.sarkariexamai.com",
                user: "253@sarkariexamai.com",
                password: "UpU?/4nzcr9BhMy",
                secure: false
            });
            console.log("✅ SUCCESS: Logged in via ftp.sarkariexamai.com!");
        } catch (err2) {
            console.log("❌ FAILED AGAIN: " + err2.message);
        }
    } finally {
        client.close();
    }
}

testConnection();
