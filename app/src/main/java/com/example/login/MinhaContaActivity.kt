package com.example.login

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView

class MinhaContaActivity : AppCompatActivity() {

    // Referências das views (usaremos findViewById diretamente)
    private lateinit var profileImage: CircleImageView
    private lateinit var tvChangePhoto: TextView
    private lateinit var editTextNome: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextCPF: EditText
    private lateinit var editTextTelefone: EditText
    private lateinit var fabSaveProfile: FloatingActionButton

    // Launcher para solicitar permissões da galeria
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(
                this,
                getString(R.string.permission_required_gallery),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Launcher para receber o resultado da seleção da imagem da galeria
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let {
                // Carregar a imagem com Glide no CircleImageView
                Glide.with(this)
                    .load(it)
                    .placeholder(R.drawable.ic_default_profile) // Opcional: imagem enquanto carrega
                    .error(R.drawable.ic_default_profile)       // Opcional: imagem se der erro
                    .into(profileImage)
            } ?: run {
                Toast.makeText(
                    this,
                    getString(R.string.image_selection_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                this,
                getString(R.string.image_loading_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minha_conta)

        // Inicializar as views
        val toolbar = findViewById<Toolbar>(R.id.toolbar_minha_conta)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.minha_conta) // Usar a string direta "minha_conta"

        profileImage = findViewById(R.id.profile_image)
        tvChangePhoto = findViewById(R.id.tv_change_photo)
        editTextNome = findViewById(R.id.editTextNome)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextCPF = findViewById(R.id.editTextCPF)
        editTextTelefone = findViewById(R.id.editTextTelefone)
        fabSaveProfile = findViewById(R.id.fab_save_profile)

        // Desabilitar campos de CPF e Telefone
        editTextCPF.isEnabled = false
        editTextTelefone.isEnabled = false

        // Exemplo: Carregar dados iniciais (simulação)
        loadProfileData()

        // Configurar listener para "Alterar Foto"
        tvChangePhoto.setOnClickListener {
            checkGalleryPermissionAndOpen()
        }

        // Configurar listener para o botão de salvar (FAB)
        fabSaveProfile.setOnClickListener {
            saveProfileData()
        }
    }

    private fun loadProfileData() {
        // Carregar dados existentes do perfil (substitua com seus dados reais)
        editTextNome.setText("Gabriel Barreto")
        editTextEmail.setText("gabriel.barreto@example.com")
        editTextCPF.setText("123.456.789-00")
        editTextTelefone.setText("(11) 99999-9999")

        // Exemplo de como você carregaria uma foto de perfil salva (se tiver uma URI)
        // val savedImageUriString = "content://media/external/images/media/..." // Obtenha esta URI do seu banco de dados/preferências
        // if (savedImageUriString.isNotEmpty()) {
        //     Glide.with(this)
        //         .load(Uri.parse(savedImageUriString))
        //         .placeholder(R.drawable.ic_default_profile)
        //         .error(R.drawable.ic_default_profile)
        //         .into(profileImage)
        // }
    }

    private fun checkGalleryPermissionAndOpen() {
        when {
            // Se a permissão já foi concedida
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE // Para Android 12 e anteriores
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_IMAGES // Para Android 13 e superiores
                    ) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }
            // Se a permissão foi negada antes e o usuário precisa de explicação
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                // Mostrar um diálogo ou Toast explicando por que a permissão é necessária
                Toast.makeText(
                    this,
                    "Precisamos de acesso à sua galeria para selecionar a foto de perfil.",
                    Toast.LENGTH_LONG
                ).show()
                // Tenta solicitar a permissão mais genérica para compatibilidade
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            // Solicitar a permissão pela primeira vez
            else -> {
                // Tenta solicitar a permissão mais genérica para compatibilidade
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(galleryIntent)
    }

    private fun saveProfileData() {
        // Aqui você obteria os dados atualizados dos campos e os salvaria
        val nomeAtualizado = editTextNome.text.toString()
        val emailAtualizado = editTextEmail.text.toString()

        // Exemplo: Salvar a URI da imagem do perfil (se houver uma nova)
        // val currentProfileImageUri = (profileImage.drawable as? BitmapDrawable)?.bitmap?.toUri() // Isso é mais complexo, exigiria salvar o bitmap em arquivo primeiro
        // Para simplificar, você salvaria a 'imageUri' recebida do picker se fosse uma imagem nova.

        // ** Importante **: Em um aplicativo real, aqui você chamaria sua lógica de negócios
        // para persistir esses dados (ex: salvar em um banco de dados, enviar para uma API).

        // Exibir mensagem de sucesso profissional
        Toast.makeText(this, getString(R.string.profile_updated_success), Toast.LENGTH_SHORT).show()

        // Opcional: Desabilitar o botão por um tempo ou mostrar um indicador de progresso
        // fabSaveProfile.isEnabled = false
        // Handler(Looper.getMainLooper()).postDelayed({ fabSaveProfile.isEnabled = true }, 2000)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // Abordagem mais moderna
        return true
    }
}