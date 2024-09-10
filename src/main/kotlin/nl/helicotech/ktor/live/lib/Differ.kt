package nl.helicotech.ktor.live.lib

sealed interface Diff {
    data class Insert(val vTag: VTag) : Diff
    data class Remove(val vTag: VTag) : Diff
    data class Replace(val old: VTag, val new: VTag) : Diff

    data class SetAttribute(val old: VTag, val new: VTag, val attr: String, val value: String): Diff
    data class RemoveAttribute(val old: VTag, val new: VTag, val attr: String): Diff
}

interface Differ<T> {
    operator fun invoke(old: T, new: T): List<Diff>
}

object VTagDiffer : Differ<VTag> {
    
    override fun invoke(old: VTag, new: VTag): List<Diff> {
        return when {
            old.fingerprint == new.fingerprint -> emptyList()
            old.tagName != new.tagName -> listOf(Diff.Replace(old, new))
            else -> VTagAttributeDiffer(old, new) + VTagListDiffer(old.children, new.children)
        }
    }
}

object VTagAttributeDiffer : Differ<VTag> {

    override fun invoke(old: VTag, new: VTag): List<Diff> {
        val diffs = mutableListOf<Diff>()

        val oldKeys = old.attributes.keys
        val newKeys = new.attributes.keys

        val both = oldKeys.intersect(newKeys)
        val removed = oldKeys.filter { !both.contains(it) }
        val added = newKeys.filter { !both.contains(it) }

        removed.forEach {
            diffs.add(Diff.RemoveAttribute(old, new, it))
        }

        added.forEach {
            diffs.add(Diff.SetAttribute(old, new, it, new.attributes[it]!!))
        }

        both
            .filter { old.attributes[it] != new.attributes[it] }
            .forEach {
                diffs.add(Diff.SetAttribute(old, new, it, new.attributes[it]!!))
            }

        return diffs
    }

}

object VTagListDiffer : Differ<List<VTag>> {
    override fun invoke(old: List<VTag>, new: List<VTag>): List<Diff> {

        val diffs = mutableListOf<Diff>()

        new.forEachIndexed { i, vTag ->
            if (i < old.size) {
                diffs.addAll(VTagDiffer(old[i], vTag))
            } else {
                diffs.add(Diff.Insert(vTag))
            }
        }

        if (new.size < old.size) {
            old.subList(new.size, old.size).forEach {
                diffs.add(Diff.Remove(it))
            }
        }

        return diffs
    }
}