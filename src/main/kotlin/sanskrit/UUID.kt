package sanskrit

data class UUID( val value: String = java.util.UUID.randomUUID().toString() ){
    override fun toString(): String = value
}