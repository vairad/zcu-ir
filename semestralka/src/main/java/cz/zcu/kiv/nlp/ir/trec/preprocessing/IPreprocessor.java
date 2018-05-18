package cz.zcu.kiv.nlp.ir.trec.preprocessing;

import java.util.List;

/**
 * Rozhraní pro službu komplexního předzpracování slov na tokeny.
 *
 * @Author Radek Vais
 */
public interface IPreprocessor {

    /**
     * Přípravná metoda očekává instnce stemmeru a tokenizátoru, pro správnou funkčnost preprocessingu.
     * V případě, že instnce nebudou null, nebudou aplikována pravidla stemmingu nebo tokenizace.
     *
     * @param stemmer - Instence stemmeru, která má být použita pro stemmování. Null pokud nechceme stemmovat.
     * @param tokenizer - Instance tokenizátoru, která má být použita pro tokenizci. Null pokud nechceme tokenizovat.
     */
    void initilise(IStemmer stemmer, ITokenizer tokenizer);

    /**
     * Metoda předzpracuje předaný text a vrátí seznam, nalezených a vytvořených tokenů.
     * @param sentence - Libovolý řetězec dokumentu, ze kterého mají být vytvořeny tokeny.
     * @return Seznam extrahovaných tokenů.
     * @throws IllegalStateException V případě, že nebyla volána metoda initialise() {@see initilise()}.
     */
    List<String> getProcessedForm(String sentence) throws IllegalStateException;

}
