
<img src="app/src/main/ic_launcher-playstore.png" align="left"
width="200" hspace="10" vspace="10">

Insider is an app which hide and retrieve encrypted text data in image.
It basically is an implementation of <a href="https://en.wikipedia.org/wiki/Steganography">steganography</a> using <a href="https://en.wikipedia.org/wiki/Advanced_Encryption_Standard">AES encryption</a>.

Insider app can be downloaded by the drive link below. Try it!!

<p align="left">
<a href="https://drive.google.com/file/d/1OsfUVFabUzfKgaw6JqVGBSOXZm_e4OcX/view?usp=sharing">
    Click here for drive link
        </a>
        </p>

## About

Insider can hide the text given in the provided image and that image user can send to anyone freely.
The same image can be decrypted and data/text can be retrieved from it using this app.

## Working

  Encryption-
  - Inputs-Key of 16 digits, Text (to be hidden), Image (in which text to be hidden)
  - Text converted to bytearray and feed to AES encryption using android encryption library.
  - Encrypted text in form bytearray converted to Base64 encoding 
  - Each chr of base64 string converted to binary value and combined as string with terminating string on both side.
  - The binary string then inserted in the image using <a href="https://www.google.com/search?client=firefox-b-d&q=lsb+method+of+steganography">LSB method</a>.
  - The final image can be shared now.
 
 Decryption-
  - Inputs- Key of 16 digits(the same that used for encryption),Image(in which data is hidden)
  - Binary data extracted from the pixels of image just by reversing the LSB method process.
  - Binary string converted back to base64 string and base64 string again decoded to bytearray.
  - Bytearray feed to the AES algo and by using the key the data is decrypted.
  - The final decrypted bytearray converted to text(utf-8) and displayed on screen.

## Features

The android app lets you:
- Hide your secret text in any image.
- The image can be shared to anyone without other getting the hint about it
- The shared image can be decoded back to get the hidden text
- Can be used for both- fun or security purposes:)
## Screenshots

[<img src="/readme/Screenshot_1604311008.png" align="left"
width="200"
    hspace="10" vspace="10">](/readme/Screenshot_16043110082.png.png)
[<img src="/readme/Screenshot_1604311219.png" align="left"
width="200"
    hspace="10" vspace="10">](/readme/Screenshot_1604311219.png)

[<img src="/readme/Screenshot_1604311262.png" align="center"
width="200"
    hspace="10" vspace="10">](/readme/Screenshot_1604311262.png)
    

## Contributors 🌟 

Thanks goes to these wonderful people ✨✨:
<table>
	<tr>
        <td align="center">
            <a href="https://github.com/Shyguy99">
              <img src="https://avatars1.githubusercontent.com/u/47186922?v=4" width="100px" alt=""/><br />
              <sub><b>Shyguy99</b></sub>
            </a><br/>
            <a href="https://github.com/Jayshah6699/datascience-mashup/commits?author=Shyguy99">
                👑 💻 👀 💬 Author
            </a>
          </td>
<td align="center">
            <a href="https://github.com/androiddevnotes">
              <img src="https://avatars2.githubusercontent.com/u/66256957?v=4" width="100px" alt=""/><br />
              <sub><b>androiddevnotes</b></sub>
            </a><br/>
            <a href="https://github.com/Jayshah6699/datascience-mashup/commits?author=androiddevnotes">
                📖
            </a>
          </td>
	</tr>
</table>

 
## Contact

For any query you can contact me via email <a href="">readytouse99@gmail.com</a> or if you have any contribution for the project you are welcome.
