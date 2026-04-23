import * as ftp from "basic-ftp";

async function deploy() {
    const client = new ftp.Client();
    client.ftp.verbose = true;

    try {
        console.log("Connecting to MilesWeb FTP...");
        client.ftp.ipFamily = 4;
        client.ftp.timeout = 120000;

        await client.access({
            host: process.env.FTP_SERVER || "103.86.176.249",
            user: process.env.FTP_USERNAME || "sarkariexam@sarkariexamai.com",
            password: process.env.FTP_PASSWORD || "15M~Ro>r5vRrL}3<",
            secure: false
        });

        // Print current directory to verify we are in the right place
        const pwd = await client.pwd();
        console.log("Current FTP directory after login:", pwd);

        // If already in /public_html (chroot), upload directly here
        // If in root (/), navigate into public_html first
        if (pwd === "/" || pwd === "") {
            console.log("Navigating into public_html...");
            await client.ensureDir("public_html");
            const pwd2 = await client.pwd();
            console.log("Now in:", pwd2);
        } else {
            console.log("Already in correct directory:", pwd, "- uploading directly...");
        }

        console.log("Uploading files from 'dist'...");
        await client.uploadFromDir("dist");

        console.log("✅ Upload Complete! Website is now live.");
    }
    catch (err) {
        console.error("Deployment failed:", err);
        process.exit(1);
    }
    finally {
        client.close();
    }
}

deploy();
