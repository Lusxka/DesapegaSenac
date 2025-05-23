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

    if (isset($_POST['PRODUTO_NOME'], $_POST['PRODUTO_DESC'], $_POST['PRODUTO_PRECO'], $_POST['PRODUTO_IMAGEM'])) {
        $produtoNome = $_POST['PRODUTO_NOME'];
        $produtoDesc = $_POST['PRODUTO_DESC'];
        $produtoPreco = $_POST['PRODUTO_PRECO'];
        $produtoImagem = $_POST['PRODUTO_IMAGEM'];

        // Inserir o produto
        $sql = "INSERT INTO PRODUTO (PRODUTO_NOME, PRODUTO_DESC, PRODUTO_PRECO, PRODUTO_IMAGEM, CATEGORIA_ID, PRODUTO_ATIVO)
                VALUES (:produtoNome, :produtoDesc, :produtoPreco, :produtoImagem, 1, 1)";
        $stmt = $pdo->prepare($sql);
        $stmt->execute([
            'produtoNome' => $produtoNome,
            'produtoDesc' => $produtoDesc,
            'produtoPreco' => $produtoPreco,
            'produtoImagem' => $produtoImagem
        ]);

        echo json_encode(['status' => 'Produto incluído com sucesso']);
    } else {
        echo json_encode(['error' => 'Dados incompletos']);
    }

} catch (PDOException $e) {
    echo "Erro de conexão: " . $e->getMessage();
    exit;
}
?>