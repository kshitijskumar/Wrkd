package org.example.project.wrkd.di.core

import org.example.project.wrkd.core.utils.CoroutinesContextProviderImpl
import org.example.project.wrkd.di.Test2
import kotlin.contracts.contract
import kotlin.reflect.KClass


object Injector {

    private var definitions: Map<String, CreationDefinition<*>> = mapOf()

    fun init(
        modules: List<Module>
    ) {
        val defs = mutableMapOf<String, CreationDefinition<*>>()
        modules.forEach { module ->
            module.definitions.entries.forEach {
                defs[it.key] = it.value
            }
        }

        definitions = defs
        println("InjecStuff: definitions: $definitions")
    }

    fun <T: Any>inject(
        name: String?,
        klass: KClass<T>,
        args: InjectionArgs
    ): T {
        val key = klass.simpleName + (name ?: "")
        val instanceFromArgs = args.firstOrNull { klass.isInstance(it) } as? T
        if (instanceFromArgs != null) {
            return instanceFromArgs
        }
        val creation = definitions[key] ?: throw IllegalStateException("No definition found for class: ${klass.simpleName} with named : $name")
        return creation.getInstance(args) as? T ?: throw IllegalStateException("No definition found for class: ${klass.simpleName} with named : $name")
    }
}

inline fun <reified T: Any>InjectionArgs.inject(name: String? = null): T {
    return Injector.inject(
        name = name,
        klass = T::class,
        args = this
    )
}

inline fun <reified T: Any>inject(
    name: String? = null,
    args: InjectionArgs = listOf()
): T {
    return args.inject(name)
}

class Module internal constructor(
    val definitions: Map<String, CreationDefinition<*>>
) {

    class Builder {

        private val _definitions = mutableMapOf<String, CreationDefinition<*>>()
        internal val definitions: Map<String, CreationDefinition<*>> get() = _definitions.toMap()

        fun <T: Any>factory(
            name: String? = null,
            klass: KClass<T>,
            block: InjectionEntry.() -> Unit = {},
            create: CREATOR<T>,
        ) {
            val definition = CreationDefinition.Factory(create = create)
            val key = klass.simpleName + (name ?: "")

            val injectionEntry = InjectionEntry { bindClasses ->
                bindClasses.forEach {
                    val keyForBinding = it.simpleName + (name ?: "")
                    _definitions[keyForBinding] = definition
                }
            }
            injectionEntry.block()
            _definitions[key] = definition
        }

        fun <T : Any>single(
            name: String? = null,
            klass: KClass<T>,
            block: InjectionEntry.() -> Unit = {},
            create: CREATOR<T>
        ) {
            val definition = CreationDefinition.Single(create = create)
            val key = klass.simpleName + (name ?: "")

            val injectionEntry = InjectionEntry { bindClasses ->
                bindClasses.forEach {
                    val keyForBinding = it.simpleName + (name ?: "")
                    _definitions[keyForBinding] = definition
                }
            }
            injectionEntry.block()
            _definitions[key] = definition
        }
    }

}

data class InjectionEntry(
    private val bind: (List<KClass<*>>) -> Unit
) {
    fun bind(vararg classes: KClass<*>) {
        bind.invoke(classes.toList())
    }
}

fun createModule(
    create: Module.Builder.() -> Unit
): Module {
    val builder = Module.Builder()
    builder.create()
    return Module(builder.definitions)
}

inline fun <reified T: Any> Module.Builder.factory(
    name: String? = null,
    noinline block: InjectionEntry.() -> Unit = {},
    noinline create: CREATOR<T>,
) {
    this.factory(
        name = name,
        klass = T::class,
        create = create,
        block = block
    )
}

inline fun <reified T: Any> Module.Builder.single(
    name: String? = null,
    noinline block: InjectionEntry.() -> Unit = {},
    noinline create: CREATOR<T>
) {
    this.single(
        name = name,
        klass = T::class,
        create = create,
        block = block
    )
}

sealed class CreationDefinition<T>(protected open val create: CREATOR<T>) {
    data class Factory<T>(
        override val create: CREATOR<T>
    ) : CreationDefinition<T>(create)

    data class Single<T>(
        override val create: CREATOR<T>
    ) : CreationDefinition<T>(create) {
        private var args = listOf<Any>()
        // will create singleton instance using first list of args passed
        private val _instance by lazy { create.invoke(args) }

        fun resolveInstance(args: InjectionArgs): T {
            this.args = args
            return _instance
        }
    }

    fun getInstance(args: List<Any>): T {
        return when(this) {
            is Factory -> this.create.invoke(args)
            is Single -> this.resolveInstance(args)
        }
    }
}

typealias CREATOR<T> = InjectionArgs.() -> T
typealias InjectionArgs = List<Any>