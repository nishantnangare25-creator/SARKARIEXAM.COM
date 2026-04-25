import * as ftp from "basic-ftp";
import * as fs from "fs";
import * as path from "path";

const FTP_CONFIG = {
    host: process.env.FTP_SERVER || "103.86.176.249",
    user: process.env.FTP_USERNAME || "sarkariexam@sarkariexamai.com",
    password: process.env.FTP_PASSWORD || "15M~Ro>r5vRrL}3<",
    secure: false,
    passive: true,
    pasvTimeout: 60000,
    connTimeout: 60000,
    keepalive: 30000
};

/**
 * Delete all files inside a remote directory (but keep the directory itself)
 */
async function clearRemoteDir(client, remoteDir) {
    let list = [];
    try { list = await client.list(remoteDir); } catch(e) { return; }

    for (const entry of list) {
        const remotePath = remoteDir + "/" + entry.name;
        if (entry.type === 2) { // directory
            await clearRemoteDir(client, remotePath);
            try { await client.removeDir(remotePath); } catch(e) {}
        } else {
            try { await client.remove(remotePath); } catch(e) {}
        }
    }
    console.log(`🗑️  Cleared: ${remoteDir}`);
}

/**
 * Upload files one-by-one with delay + per-file retry.
 */
async function uploadSlowly(client, localDir, remoteBase) {
    const entries = fs.readdirSync(localDir, { withFileTypes: true });

    for (const entry of entries) {
        const localPath = path.join(localDir, entry.name);
        const remotePath = remoteBase + "/" + entry.name;

        if (entry.isDirectory()) {
            console.log(`📂 Creating Directory: ${entry.name}`);
            await client.ensureDir(remotePath);
            await uploadSlowly(client, localPath, remotePath);
        } else {
            let uploaded = false;
            for (let attempt = 1; attempt <= 3; attempt++) {
                try {
                    process.stdout.write(`📄 Uploading: ${entry.name.padEnd(42)}`);
                    await client.uploadFrom(localPath, remotePath);
                    console.log(" [DONE]");
                    uploaded = true;
                    break;
                } catch (fileErr) {
                    console.log(` [RETRY ${attempt}/3] ${fileErr.message}`);
                    await new Promise(r => setTimeout(r, 3000 * attempt));
                }
            }
            if (!uploaded) throw new Error(`Failed to upload ${entry.name}`);
            await new Promise(r => setTimeout(r, 600));
        }
    }
}

async function deployWithRetry(maxRetries = 5) {
    for (let attempt = 1; attempt <= maxRetries; attempt++) {
        const client = new ftp.Client();
        client.ftp.ipFamily = 4;
        client.ftp.timeout = 300000;

        try {
            console.log(`\n🚀 Deployment attempt ${attempt}/${maxRetries}...`);
            await client.access(FTP_CONFIG);
            console.log("✅ Connected to FTP server.");

            const REMOTE_ROOT = "/public_html";

            // Step 1: Clean old assets (removes all stale JS/CSS files)
            console.log("\n🗑️  Cleaning old assets folder...");
            await clearRemoteDir(client, REMOTE_ROOT + "/assets");

            // Step 2: Upload fresh build
            console.log("\n📤 Uploading fresh build...");
            await uploadSlowly(client, "dist", REMOTE_ROOT);

            console.log("\n✅ SUCCESS: Website is now live on MilesWeb.");
            client.close();
            return;
        } catch (err) {
            console.error(`❌ Attempt ${attempt} failed:`, err.message);
            client.close();

            if (attempt === maxRetries) {
                console.error("⛔ Max retries reached. Deployment failed.");
                throw err;
            }

            const delay = attempt * 15000;
            console.log(`🔄 Waiting ${delay / 1000}s before next attempt...`);
            await new Promise(resolve => setTimeout(resolve, delay));
        }
    }
}

console.log("Starting Clean Deploy for MilesWeb (BitNinja Compatible)...");
deployWithRetry().catch(err => {
    console.error("FATAL: Deployment failed:", err.message);
    process.exit(1);
});
