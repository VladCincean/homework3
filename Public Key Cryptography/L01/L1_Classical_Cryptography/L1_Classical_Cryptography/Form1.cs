using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

using System.Text.RegularExpressions;

namespace L1_Classical_Cryptography
{
    public partial class Form1 : Form
    {
        private string alphabet = " abcdefghijklmnopqrstuvwxyz";
        private enum StringType { KEY, TEXT };

        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {

        }

        /**
         *  Checks if a given char is contained in the alphabet (global variable).
         *  The check is case insensitive.
         *  Input:  a character
         *  Output: true, if c in alphabet
         *          false, otherwise
         */
        private bool ValidateCharInAlphabet(char c)
        {
            foreach (char a in alphabet)
            {
                if (a == Char.ToLower(c))
                {
                    return true;
                }
            }

            return false;
        }

        /**
         *  Validates a string.
         *  Input:  str - a string
         *          type -  KEY,    if encryption key or decryption key
         *                  TEXT,   if plaintext or ciphertext
         *  Output: true, if key is valid
         *          false, if key is not valid
         */
        private bool ValidateString(string str, StringType type)
        {
            if (type == StringType.KEY)
            {
                // validate the length
                if (str.Length != 27)
                {
                    return false;
                }
            }

            // validate the characters
            foreach (char c in str)
            {
                if (false == ValidateCharInAlphabet(c))
                {
                    return false;
                }
            }

            if (type == StringType.KEY)
            {
                // validate the uniqueness
                for (int i = 0; i < str.Length - 1; i++)
                {
                    for (int j = i + 1; j < str.Length; j++)
                    {
                        if (Char.ToLower(str[i]) == Char.ToLower(str[j]))
                        {
                            return false;
                        }
                    }
                }
            }

            return true;
        }

        /**
         *  Given an encryption key, provides the corresponding decryption key.
         *  Input:  EncryptionKey
         *  Output: DecryptionKey
         */
        private string GetDecryptionKey(string EncryptionKey)
        {
            string DecryptionKey = null;

            // validate the key
            if (false == ValidateString(EncryptionKey, StringType.KEY))
            {
                return null;
            }

            // compute the corresponding decryption key
            DecryptionKey = "";
            for (int i = 0; i < alphabet.Length; i++)
            {
                for (int j = 0; j < EncryptionKey.Length; j++)
                {
                    if (Char.ToLower(EncryptionKey[j]) == alphabet[i])
                    {
                        DecryptionKey += alphabet[j];
                        break;
                    }
                }
            }

            return DecryptionKey;
        }

        /**
         *  Given an encryption key and a plaintext, encrypts the plaintext
         *  Input:  Plaintext (lowercase)
         *          EncryptionKey
         *  Output: Ciphertext (uppercase)
         *          null, if EncryptionKey or Plaintext not valid
         */
        private string EncryptPlaintext(string Plaintext, string EncryptionKey)
        {
            string Ciphertext = null;

            // validate
            if (false == ValidateString(EncryptionKey, StringType.KEY) ||
                false == ValidateString(Plaintext, StringType.TEXT))
            {
                return null;
            }

            // encrypt the plaintext
            Ciphertext = "";
            for (int i = 0; i < Plaintext.Length; i++)
            {
                char c = Char.ToLower(Plaintext[i]);
                Ciphertext += EncryptionKey[alphabet.IndexOf(c)];
            }

            return Ciphertext.ToUpper();
        }

        /**
         *  Given an encryption key and a ciphertext, computes the decryption key and then decrypts the ciphertext.
         *  Input:  Ciphertext (uppercase)
         *          EncryptionKey
         *  Output: Plaintext (lowercase)
         *          null, if EncryptionKey or Ciphertext not valid
         */
        private string DecryptCiphertext(string Ciphertext, string EncryptionKey)
        {
            string Plaintext = null;
            string DecryptionKey = null;

            // validate
            if (false == ValidateString(EncryptionKey, StringType.KEY) ||
                false == ValidateString(Ciphertext, StringType.TEXT))
            {
                return null;
            }

            // compute the decryption key
            DecryptionKey = GetDecryptionKey(EncryptionKey);

            // decrypt the ciphertext
            Plaintext = "";
            for (int i = 0; i < Ciphertext.Length; i++)
            {
                char c = Char.ToLower(Ciphertext[i]);
                Plaintext += DecryptionKey[alphabet.IndexOf(c)];
            }

            return Plaintext;
        }

        private void textBox_EncryptionKey_TextChanged(object sender, EventArgs e)
        {
            // prevalidate textBox_EncryptionKey
            if (Regex.IsMatch(textBox_EncryptionKey.Text, "^([A-Z]| )*$"))
            {
                textBox_EncryptionKey.ForeColor = Color.Green;
            }
            else
            {
                textBox_EncryptionKey.Text = textBox_EncryptionKey.Text.Remove(textBox_EncryptionKey.Text.Length - 1);
                textBox_EncryptionKey.SelectionStart = textBox_EncryptionKey.Text.Length;
            }
            for (int i = 0; i < textBox_EncryptionKey.Text.Length - 1; i++)
            {
                for (int j = i + 1; j < textBox_EncryptionKey.Text.Length; j++)
                {
                    if (Char.ToLower(textBox_EncryptionKey.Text[i]) == Char.ToLower(textBox_EncryptionKey.Text[j]))
                    {
                        textBox_EncryptionKey.Text = textBox_EncryptionKey.Text.Remove(j) + textBox_EncryptionKey.Text.Substring(j + 1);
                        textBox_EncryptionKey.SelectionStart = textBox_EncryptionKey.Text.Length;
                        break;
                    }
                }
            }

            // prefill listView_EncryptionKey
            int k = 0;
            for (; k < textBox_EncryptionKey.Text.Length; k++)
            {
                listView_EncryptionKey.Items[0].SubItems[k].Text = "" + Char.ToUpper(textBox_EncryptionKey.Text[k]);
            }
            for (; k < alphabet.Length; k++)
            {
                listView_EncryptionKey.Items[0].SubItems[k].Text = "";
            }

            // prefill listView_DecryptionKey
            for (k = 0; k < alphabet.Length; k++)
            {
                listView_DecryptionKey.Items[0].SubItems[k].Text = "";
            }
            for (k = 0; k < textBox_EncryptionKey.Text.Length; k++)
            {
                char c = Char.ToLower(textBox_EncryptionKey.Text[k]);
                listView_DecryptionKey.Items[0].SubItems[alphabet.IndexOf(c)].Text = "" + alphabet[k];
            }
        }

        private void button_ClearAll_Click(object sender, EventArgs e)
        {
            textBox_EncryptionKey.Text = "";
            textBox_Plaintext.Text = "";
            textBox_Ciphertext.Text = "";
        }

        private void textBox_Plaintext_TextChanged(object sender, EventArgs e)
        {
            // prevalidate textBox_EncryptionKey
            if (!Regex.IsMatch(textBox_Plaintext.Text, "^([a-z]| )*$"))
            {
                textBox_Plaintext.Text = textBox_Plaintext.Text.Remove(textBox_Plaintext.Text.Length - 1);
                textBox_Plaintext.SelectionStart = textBox_Plaintext.Text.Length;
            }
        }

        private void textBox_Ciphertext_TextChanged(object sender, EventArgs e)
        {
            // prevalidate textBox_EncryptionKey
            if (!Regex.IsMatch(textBox_Ciphertext.Text, "^([A-Z]| )*$"))
            {
                textBox_Ciphertext.Text = textBox_Ciphertext.Text.Remove(textBox_Ciphertext.Text.Length - 1);
                textBox_Ciphertext.SelectionStart = textBox_Ciphertext.Text.Length;
            }
        }

        private void button_Encrypt_Click(object sender, EventArgs e)
        {
            string EncryptionKey = textBox_EncryptionKey.Text;
            string Plaintext = textBox_Plaintext.Text;

            string Ciphertext = EncryptPlaintext(Plaintext, EncryptionKey);
            if (null == Ciphertext)
            {
                MessageBox.Show("EncryptionKey or Plaintext not valid!");
            }
            else
            {
                textBox_Ciphertext.Text = Ciphertext;
            }
        }

        private void button_Decrypt_Click(object sender, EventArgs e)
        {
            string EncryptionKey = textBox_EncryptionKey.Text;
            string Ciphertext = textBox_Ciphertext.Text;

            string Plaintext = DecryptCiphertext(Ciphertext, EncryptionKey);
            if (null == Plaintext)
            {
                MessageBox.Show("EncryptionKey or Ciphertext not valid!");
            }
            else
            {
                textBox_Plaintext.Text = Plaintext;
            }
        }
    }
}
