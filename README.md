# SMTP Maleficent Trickster Program

This program serves as a tool for running prank campaigns on known e-mail addresses.

Given a file containing addresses, a file containing messages and a group count,
it composes groups of 2-5 addresses. In a given group, one address is designated
as the sender, and sends a message picked at random from the messages file to all
the other addresses in the group.


## Mock server setup


You can use any mock SMTP server software. As an example, here's how to set up a
devmail server on docker:

`docker run -d -p 1080:1080 -p 1025:1025 maildev/maildev`


## Run the tool

In order to run the SMTP Maleficent Trickster Program, you have to write a `config` file in order to provide the server ip address and port, the subject
of the messages, the group count, and the paths to the addresses and messages files.
You can do that with any text editor like Notepad, TextEdit or Visual Studio Code.

The config file is located at `./.config` by default, whrere `.` is the working directory. \
You can provide an alternative path by using the `-config <path>` option.

The config file follows the following structure:
```
host: <host_ip>
port: <host_port>
addresses: <addresses_file_path>
messages: <messages_file_path>
subject: <message_subject>
groups: <group_count>
```

## Implementation details

### SMTPSender
The main part of the implementation lies in the SMTPSender class, which implements the
`Closeable` interface. This allows for standardized use of the `.close()` method,
as well as the try-with-resources syntax. The `close()` method takes care of closing
internal stream objects, as well as the SMTP connection according to the protocol.

### Util
This class contains various helper methods, and cannot be instantiated.