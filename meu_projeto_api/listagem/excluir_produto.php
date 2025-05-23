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

    if (isset($_POST['PRODUTO_ID'])) {
        $produtoId = $_POST['PRODUTO_ID'];

        // Deletar o produto
        $sql = "DELETE FROM PRODUTO WHERE PRODUTO_ID = :produtoId";
        $stmt = $pdo->prepare($sql);
        $stmt->execute(['produtoId' => $produtoId]);

        echo json_encode(['status' => 'Produto deletado com sucesso']);
    } else {
        echo json_encode(['error' => 'ID do produto não informado']);
    }

} catch (PDOException $e) {
    echo "Erro de conexão: " . $e->getMessage();
    exit;
}
?>