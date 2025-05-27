<?php
$host = 'www.thyagoquintas.com.br';
$db   = 'engenharia_17';
$user = 'engenharia_17';
$pass = 'falcaoperegrino';
$charset = 'utf8mb4';

$dsn = "mysql:host=$host;dbname=$db;charset=$charset";
$options = [
    PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    PDO::ATTR_EMULATE_PREPARES   => false,
];

try {
    $pdo = new PDO($dsn, $user, $pass, $options);

    if (isset($_GET['usuario']) && isset($_GET['senha'])) {
        // Processo de login
        $usuario = $_GET['usuario'];
        $senha = $_GET['senha'];

        $sql = "SELECT USUARIO_ID as usuarioId, USUARIO_NOME as usuarioNome, USUARIO_EMAIL as usuarioEmail, USUARIO_SENHA as usuarioSenha, USUARIO_CPF as usuarioCpf, USUARIO_ADM as usuarioAdm
                FROM USUARIO 
                WHERE USUARIO_EMAIL = :usuario";

        $stmt = $pdo->prepare($sql);
        $stmt->execute(['usuario' => $usuario]);
        $usuarios = $stmt->fetchAll();

        if (count($usuarios) > 0) {
            $usuarioBanco = $usuarios[0];

            if ($senha === $usuarioBanco['usuarioSenha']) {
                error_log("Login bem-sucedido: " . json_encode([$usuarioBanco]));
                echo json_encode([$usuarioBanco]);
            } else {
                echo json_encode([]); // Senha incorreta
            }
        } else {
            echo json_encode([]); // Usuário não encontrado
        }

    } else {
        echo json_encode(["erro" => "Parâmetros inválidos."]);
    }

} catch (\PDOException $e) {
    echo json_encode(["erro" => "Erro de conexão: " . $e->getMessage()]);
    exit;
}
?>