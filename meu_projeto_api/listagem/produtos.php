<?php
header('Content-Type: application/json; charset=utf-8');

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
    PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"
];

try {
    $pdo = new PDO($dsn, $user, $pass, $options);

    // Revertendo a consulta SQL para como estava originalmente
    // sem o JOIN com a tabela USUARIO e sem o USUARIO_TELEFONE
    $sql = "SELECT
                PRODUTO_ID,
                PRODUTO_NOME,
                PRODUTO_DESC,
                PRODUTO_PRECO,
                PRODUTO_DESCONTO,
                CATEGORIA_ID,
                PRODUTO_ATIVO,
                PRODUTO_IMAGEM
            FROM PRODUTO";

    $stmt = $pdo->prepare($sql);
    $stmt->execute();
    $produtos = $stmt->fetchAll();

    echo json_encode($produtos);

} catch (\PDOException $e) {
    http_response_code(500); // Internal Server Error
    echo json_encode(["erro" => "Erro de conexão: " . $e->getMessage()]);
    exit;
}
?>