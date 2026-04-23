import * as ftp from "basic-ftp";

async function deploy() {
    const client = new ftp.Client();
    client.ftp.verbose = true; // Log progress
    try {
        console.log("Connecting to MilesWeb FTP...");
        client.ftp.ipFamily = 4; // Force IPv4
        client.ftp.timeout = 60000; // 1 minute timeout
        
        await client.access({
            host: process.env.FTP_SERVER || "sarkariexamai.com",
            user: process.env.FTP_USERNAME,
            password: process.env.FTP_PASSWORD,
            secure: false
        });

        console.log("Connected! Navigating to public_html...");
        await client.ensureDir("public_html"); // Better than cd
        
        console.log("Cleaning old files (optional) and uploading new files from 'dist'...");
        // Use uploadFromDir which is more robust
        await client.uploadFromDir("dist");
        
        console.log("Upload Complete! The new files are now live.");
    }
    catch (err) {
        console.error("Deployment failed:", err);
        process.exit(1); // Force GitHub Action to show failure if deployment fails
    }
    client.close();
}

deploy();
