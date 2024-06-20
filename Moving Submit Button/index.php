<html>
    <head>
        <link rel="stylesheet" type="text/css" href="../Moving Submit Button/style.css">
    </head>
    <div class="container">
        <?php
        // Connect to the database
        $servername = "localhost";
        $username = "root";
        $password = "";
        $dbname = "copy";

        $conn = new mysqli($servername, $username, $password, $dbname);
        if ($conn->connect_error) {
            die("Connection failed: " . $conn->connect_error);
        }

        // Query the database
        $sql = "SELECT * FROM user";
        $result = $conn->query($sql);

        // Close the database connection
        $conn->close();
        ?>
        <h2>Naughty Submit Button</h2>        
        <input type="text" placeholder="Username" id="username">
        <input type="password" placeholder="Password" id="password">
        <button id="submit">Submit</button>
        <p id="message_alert">Signed Up Successfully!</p>
    </div>
    <script src="../Moving Submit Button/script.js"></script>
</html>