import * as ftp from "basic-ftp";

async function deployWithRetry(maxRetries = 3) {
    for (let attempt = 1; attempt <= maxRetries; attempt++) {
        const client = new ftp.Client();
        client.ftp.verbose = true;
        
        try {
            console.log(`\n🚀 Deployment attempt ${attempt}/${maxRetries}...`);
            client.ftp.ipFamily = 4;
            client.ftp.timeout = 120000; // 2 minutes

            await client.access({
                host: process.env.FTP_SERVER || "103.86.176.249",
                user: process.env.FTP_USERNAME || "sarkariexam@sarkariexamai.com",
                password: process.env.FTP_PASSWORD || "15M~Ro>r5vRrL}3<",
                secure: false
            });

            const pwd = await client.pwd();
            console.log("Current FTP directory:", pwd);

            if (pwd === "/" || pwd === "") {
                console.log("Navigating into public_html...");
                await client.ensureDir("public_html");
            }
            
            console.log("Uploading files from 'dist' to MilesWeb...");
            await client.uploadFromDir("dist");
            
            console.log("✅ SUCCESS: Upload Complete! Website is now live.");
            client.close();
            return; // Exit on success
        } 
        catch (err) {
            console.error(`❌ Attempt ${attempt} failed:`, err.message);
            client.close();
            
            if (attempt === maxRetries) {
                console.error("⛔ Max retries reached. Deployment failed.");
                throw err;
            }
            
            const delay = attempt * 5000;
            console.log(`🔄 Retrying in ${delay / 1000} seconds...`);
            await new Promise(resolve => setTimeout(resolve, delay));
        }
    }
}

deployWithRetry().catch(err => {
    console.error("FATAL: Deployment failed after multiple attempts.");
    process.exit(1);
});
