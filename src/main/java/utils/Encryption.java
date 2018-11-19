package utils;

public final class Encryption {

  public static String encryptDecryptXOR(String rawString) {

    // If encryption is enabled in Config.
    if (Config.getEncryption()) {

      // The key is predefined and hidden in code
      // TODO: Create a more complex code and store it somewhere better (umiddelbart færdig)
      char[] key = Config.getKey().toCharArray();

      // Stringbuilder enables you to play around with strings and make useful stuff
      StringBuilder thisIsEncrypted = new StringBuilder();

      // TODO: This is where the magic of XOR is happening. Are you able to explain what is going on?
      for (int i = 0; i < rawString.length(); i++) {   //dette er et loop, som går fra i=0 og 1 op (i++) indtil at længden/antal tegn på rawString er nået.
        thisIsEncrypted.append((char) (rawString.charAt(i) ^ key[i % key.length])); //This is encrypted er et objekt af Stringbuilder som gør det muligt at bruge metoden append,
                                                                                    // som hver gang der findes et tegn i rawString, tilføjer append (skriv videre /ikke færdig)

      }

      // We return the encrypted string
      return thisIsEncrypted.toString();

    } else {
      // We return without having done anything
      return rawString;
    }
  }
}
