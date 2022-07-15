const express = require('express')
const https = require("https")
const env = require('dotenv').config()

const app = express()
const port = 9005

var options = {
    hostname: "eu.battle.net",
    path: '/oauth/token',
    method: 'POST',
    auth: process.env.CLIENT_ID + ":" + process.env.CLIENT_SECRET,
    headers: {
        "Content-Type": "application/x-www-form-urlencoded"
    }
}

var grant_type = Buffer.from("grant_type").toString("base64") + "=" + Buffer.from("client_credentials").toString("base64")

app.get('/', (req, res) => {
    var access_token_req = https.request(options, function(response) {
        console.log('STATUS: ' + response.statusCode);
        console.log('HEADERS: ' + JSON.stringify(response.headers));
        response.setEncoding('utf8');

        let body = ''
        response.on('data', function(chunk) {
            body += chunk
        })

        response.on('end', () => {
            console.log(body)
            res.send(body)
        })
    })

    access_token_req.write("grant_type=client_credentials")
    access_token_req.end()
})

app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)
})