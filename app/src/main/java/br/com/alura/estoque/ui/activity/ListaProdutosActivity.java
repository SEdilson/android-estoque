package br.com.alura.estoque.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import br.com.alura.estoque.R;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.repository.ProdutoRepository;
import br.com.alura.estoque.ui.dialog.EditaProdutoDialog;
import br.com.alura.estoque.ui.dialog.SalvaProdutoDialog;
import br.com.alura.estoque.ui.recyclerview.adapter.ListaProdutosAdapter;

public class ListaProdutosActivity extends AppCompatActivity {

    private static final String TITULO_APPBAR = "Lista de produtos";
    private static final String MENSAGEM_ERRO_BUSCA_PRODUTOS = "Não foi possível carregar os produtos novos.";
    private static final String MENSAGEM_ERRO_REMOCAO_PRODUTO = "Não foi possível remover o produto.";
    private static final String MENSAGEM_ERRO_SALVA_PRODUTO = "Não foi possível salvar o produto.";
    private static final String MENSAGEM_ERRO_EDITA_PRODUTO = "Não foi possível editar o produto";
    private ListaProdutosAdapter adapter;
    private ProdutoRepository repository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produtos);
        setTitle(TITULO_APPBAR);

        configuraListaProdutos();
        configuraFabSalvaProduto();

        repository = new ProdutoRepository(this);
        configuraBuscaProdutos();
    }

    private void configuraBuscaProdutos() {
        repository.buscaProdutos(new ProdutoRepository.DadosCarregadosCallback<List<Produto>>() {
            @Override
            public void quandoSucesso(List<Produto> resposta) {
                adapter.atualiza(resposta);
            }

            @Override
            public void quandoErro(String erro) {
                Toast.makeText(ListaProdutosActivity.this,
                        MENSAGEM_ERRO_BUSCA_PRODUTOS,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configuraListaProdutos() {
        RecyclerView listaProdutos = findViewById(R.id.activity_lista_produtos_lista);
        adapter = new ListaProdutosAdapter(this, this::abreFormularioEditaProduto);
        listaProdutos.setAdapter(adapter);
        configuraRemocaoProduto();
    }

    private void configuraRemocaoProduto() {
        adapter.setOnItemClickRemoveContextMenuListener(((posicao, produtoSelecionado) -> {
            repository.remove(produtoSelecionado, new ProdutoRepository.DadosCarregadosCallback<Void>() {
                @Override
                public void quandoSucesso(Void resposta) {
                    adapter.remove(posicao);
                }

                @Override
                public void quandoErro(String erro) {
                    Toast.makeText(ListaProdutosActivity.this,
                            MENSAGEM_ERRO_REMOCAO_PRODUTO,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }));
    }

    private void configuraFabSalvaProduto() {
        FloatingActionButton fabAdicionaProduto = findViewById(R.id.activity_lista_produtos_fab_adiciona_produto);
        fabAdicionaProduto.setOnClickListener(v -> abreFormularioSalvaProduto());
    }

    private void abreFormularioSalvaProduto() {
        new SalvaProdutoDialog(this, produtoCriado ->
                configuraSalvarProduto(produtoCriado))
                .mostra();
    }

    private void configuraSalvarProduto(Produto produto) {
        repository.salva(produto, new ProdutoRepository.DadosCarregadosCallback<Produto>() {
            @Override
            public void quandoSucesso(Produto resposta) {
                adapter.adiciona(produto);
            }

            @Override
            public void quandoErro(String erro) {
                Toast.makeText(ListaProdutosActivity.this,
                        MENSAGEM_ERRO_SALVA_PRODUTO,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abreFormularioEditaProduto(int posicao, Produto produto) {
        new EditaProdutoDialog(this, produto,
                produtoEditado -> repository.edita(produtoEditado,
                        configuraEditarProduto(posicao)))
                .mostra();
    }

    @NotNull
    private ProdutoRepository.DadosCarregadosCallback<Produto> configuraEditarProduto(int posicao) {
        return new ProdutoRepository.DadosCarregadosCallback<Produto>() {
    @Override
    public void quandoSucesso(Produto resposta) {
        adapter.edita(posicao, resposta);
    }

    @Override
    public void quandoErro(String erro) {
        Toast.makeText(ListaProdutosActivity.this,
                MENSAGEM_ERRO_EDITA_PRODUTO,
                Toast.LENGTH_SHORT).show();
    }
};
    }
}
