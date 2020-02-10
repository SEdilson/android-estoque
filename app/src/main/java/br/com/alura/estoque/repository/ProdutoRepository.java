package br.com.alura.estoque.repository;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import br.com.alura.estoque.asynctask.BaseAsyncTask;
import br.com.alura.estoque.database.dao.ProdutoDAO;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.retrofit.EstoqueRetrofit;
import br.com.alura.estoque.retrofit.service.ProdutoService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class ProdutoRepository {

    private final ProdutoDAO dao;
    private ProdutoService estoqueService;

    public ProdutoRepository(ProdutoDAO dao) {
        this.dao = dao;
        estoqueService = new EstoqueRetrofit().getProdutoService();
    }

    public void buscaProdutos(DadosCarregadosListener<List<Produto>> listener) {
        buscaProdutosInternos(listener);
    }

    private void buscaProdutosInternos(DadosCarregadosListener<List<Produto>> listener) {
        new BaseAsyncTask<>(dao::buscaTodos,
                resultado -> {
                    listener.quandoCarregado(resultado);
                    buscaProdutosNaApi(listener);
                }).execute();
    }

    private void buscaProdutosNaApi(DadosCarregadosListener<List<Produto>> listener) {
        Call<List<Produto>> call = estoqueService.buscaTodos();

        new BaseAsyncTask<>(() -> {
            try {
                Response<List<Produto>> resposta = call.execute();
                List<Produto> produtosNovos = resposta.body();
                dao.salva(produtosNovos);
            } catch (IOException e) {
                e.getStackTrace();
            }
            return dao.buscaTodos();
        }, listener::quandoCarregado)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void salva(Produto produto,
                      DadosCarregadosCallback<Produto> callback) {
        salvaNaApi(produto, callback);
    }

    private void salvaNaApi(Produto produto,
                            DadosCarregadosCallback<Produto> callback) {
        Call<Produto> call = estoqueService.salva(produto);
        call.enqueue(new Callback<Produto>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<Produto> call,
                                   Response<Produto> response) {
                if(response.isSuccessful()) {
                    Produto produtoSalvo = response.body();
                    if(produtoSalvo != null) {
                        salvaInterno(produtoSalvo, callback);
                    }
                } else {
                    callback.quandoErro("Resposta não sucessida.");
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<Produto> call,
                                  Throwable t) {
                callback.quandoErro("Falha na comunicação:" + t.getMessage());
            }
        });
    }

    private void salvaInterno(Produto produto, DadosCarregadosCallback<Produto> callback) {
        new BaseAsyncTask<>(() -> {
            long id = dao.salva(produto);
            return dao.buscaProduto(id);
        }, callback::quandoSucesso)
                .execute();
    }

    public interface DadosCarregadosListener<T> {
        void quandoCarregado(T produtos);
    }

    public interface DadosCarregadosCallback<T> {
        void quandoSucesso(T resposta);
        void quandoErro(String erro);
    }

}
