package com.example.listacompra

data class ItemLista(
    var nome: String,
    var categoria: String = "Geral",
    var grupo: String = "Mercado", // Mercado, Granel, Roupas, etc.
    var data: String,
    var quantidade: Int = 1,
    var marcado: Boolean = false
)

object MockData {
    val gruposIniciais = mutableListOf("Mercado", "Granel", "Roupas", "Farmácia")

    val categorias = listOf(
        "Frutas & Legumes", "Padaria", "Laticínios", "Carnes",
        "Limpeza", "Higiene", "Bebidas", "Mercearia", "Congelados", "Pet Shop"
    )

    val produtosPorCategoria = mapOf(
        "Frutas & Legumes" to listOf("Banana", "Maçã", "Tomate", "Cebola", "Batata"),
        "Padaria" to listOf("Pão Francês", "Pão de Forma", "Bolo", "Pão de Queijo", "Rosquinha"),
        "Laticínios" to listOf("Leite", "Queijo Mussarela", "Iogurte", "Manteiga", "Requeijão"),
        "Carnes" to listOf("Frango", "Carne Moída", "Linguiça", "Peixe", "Bacon"),
        "Limpeza" to listOf("Detergente", "Sabão em Pó", "Amaciante", "Desinfetante", "Água Sanitária"),
        "Higiene" to listOf("Papel Higiênico", "Creme Dental", "Sabonete", "Shampoo", "Desodorante"),
        "Bebidas" to listOf("Água", "Refrigerante", "Suco", "Cerveja", "Café"),
        "Mercearia" to listOf("Arroz", "Feijão", "Açúcar", "Óleo", "Macarrão"),
        "Congelados" to listOf("Pizza", "Lasanha", "Hambúrguer", "Batata Frita", "Sorvete"),
        "Pet Shop" to listOf("Ração Gato", "Ração Cão", "Areia Sanitária", "Petisco", "Shampoo Pet")
    )
}