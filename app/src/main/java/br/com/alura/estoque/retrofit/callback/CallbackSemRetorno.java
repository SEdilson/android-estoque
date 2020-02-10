package br.com.alura.estoque.retrofit.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static br.com.alura.estoque.retrofit.callback.MensagensCallback.MENSAGEM_FALHA_NA_COMUNICAÇÃO;
import static br.com.alura.estoque.retrofit.callback.MensagensCallback.MENSAGEM_RESPOSTA_NÃO_SUCEDIDA;

public class CallbackSemRetorno implements Callback<Void> {

    private final RespostaCallback callback;

    public CallbackSemRetorno(RespostaCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onResponse(Call<Void> call, Response<Void> response) {
        if(response.isSuccessful()) {
            callback.quandoSucesso();
        } else {
            callback.quandoErro(MENSAGEM_RESPOSTA_NÃO_SUCEDIDA);
        }
    }

    @Override
    public void onFailure(Call<Void> call, Throwable t) {
        callback.quandoErro(MENSAGEM_FALHA_NA_COMUNICAÇÃO + t.getMessage());
    }

    public interface RespostaCallback {
        void quandoSucesso();
        void quandoErro(String erro);
    }
}
