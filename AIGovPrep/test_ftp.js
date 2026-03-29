import * as ftp from "basic-ftp";

async function exploreFTP() {
    const client = new ftp.Client()
    try {
        await client.access({
            host: "sarkariexamai.com",
            user: "253@sarkariexamai.com",
            password: "NA45&#$re123",
            secure: false
        })
        console.log("Root directory '/p':")
        const listRoot = await client.list("/")
        console.log(listRoot.map(f => f.name + (f.isDirectory ? "/" : "")).join(", "))
        
    }
    catch(err) {
        console.error(err)
    }
    client.close()
}

exploreFTP();
