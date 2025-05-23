<?php
// Configurações do Banco de Dados
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

    // Permitir requisições de origens diferentes (CORS)
    header("Access-Control-Allow-Origin: *");
    header("Access-Control-Allow-Methods: POST, GET, OPTIONS");
    header("Access-Control-Allow-Headers: Content-Type");
    header("Content-Type: application/json"); // Define o tipo de conteúdo da resposta

    // Se a requisição for OPTIONS (pré-voo), apenas encerre
    if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
        exit(0);
    }

    // Verifica se a requisição é um POST e se os parâmetros esperados estão presentes
    if ($_SERVER['REQUEST_METHOD'] === 'POST' &&
        isset($_POST['USUARIO_NOME']) &&
        isset($_POST['USUARIO_EMAIL']) &&
        isset($_POST['USUARIO_SENHA']) &&
        isset($_POST['USUARIO_CPF']) &&
        isset($_POST['USUARIO_TELEFONE'])) {

        $nome = $_POST['USUARIO_NOME'];
        $email = $_POST['USUARIO_EMAIL'];
        $senha = $_POST['USUARIO_SENHA']; // Lembre-se: MELHORAR A SEGURANÇA COM HASH!
        $cpf = $_POST['USUARIO_CPF'];
        $telefone = $_POST['USUARIO_TELEFONE'];
        $adm = 0; // Define como usuário comum por padrão no cadastro

        // 1. Verificar se o e-mail já existe
        $stmt_check_email = $pdo->prepare("SELECT USUARIO_ID FROM USUARIO WHERE USUARIO_EMAIL = :email");
        $stmt_check_email->execute(['email' => $email]);
        if ($stmt_check_email->rowCount() > 0) {
            http_response_code(409); // Conflict
            echo json_encode(["message" => "E-mail já cadastrado."]);
            exit();
        }

        // 2. Inserir o novo usuário no banco de dados
        $sql = "INSERT INTO USUARIO (USUARIO_NOME, USUARIO_EMAIL, USUARIO_SENHA, USUARIO_CPF, USUARIO_TELEFONE, USUARIO_ADM)
                VALUES (:nome, :email, :senha, :cpf, :telefone, :adm)";
        
        $stmt = $pdo->prepare($sql);
        $result = $stmt->execute([
            'nome' => $nome,
            'email' => $email,
            'senha' => $senha, // **ATENÇÃO: MUDAR PARA password_hash() EM PRODUÇÃO**
            'cpf' => $cpf,
            'telefone' => $telefone,
            'adm' => $adm
        ]);

        if ($result) {
            http_response_code(201); // Created
            echo json_encode(["message" => "Usuário cadastrado com sucesso!"]);
        } else {
            http_response_code(500); // Internal Server Error
            echo json_encode(["message" => "Erro ao cadastrar usuário."]);
        }

    } else {
        http_response_code(400); // Bad Request
        echo json_encode(["message" => "Parâmetros inválidos ou requisição não é POST."]);
    }

} catch (\PDOException $e) {
    http_response_code(500); // Internal Server Error
    echo json_encode(["message" => "Erro de conexão com o banco de dados: " . $e->getMessage()]);
}
?>