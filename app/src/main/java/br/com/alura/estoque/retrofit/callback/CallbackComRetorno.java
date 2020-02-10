package br.com.alura.estoque.retrofit.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.alura.estoque.retrofit.callback.MensagensCallback.MENSAGEM_FALHA_NA_COMUNICAÇÃO;
import static br.com.alura.estoque.retrofit.callback.MensagensCallback.MENSAGEM_RESPOSTA_NÃO_SUCEDIDA;

public class CallbackComRetorno<T> implements Callback<T> {

    private final RespostaCallback callback;

    public CallbackComRetorno(RespostaCallback<T> callback) {
        this.callback = callback;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if(response.isSuccessful()) {
            T resultado = response.body();
            if(resultado != null) {
                callback.quandoSucesso(resultado);
            }
        } else {
            callback.quandoFalha(MENSAGEM_RESPOSTA_NÃO_SUCEDIDA);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        callback.quandoFalha(MENSAGEM_FALHA_NA_COMUNICAÇÃO  + t.getMessage());
    }

    public interface RespostaCallback<T> {
        void quandoSucesso(T resultado);
        void quandoFalha(String erro);
    }
}
