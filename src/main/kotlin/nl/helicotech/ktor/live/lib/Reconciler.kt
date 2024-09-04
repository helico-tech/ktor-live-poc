package nl.helicotech.ktor.live.lib

class Reconciler {

    sealed interface Diff {
        data class Insert(val vTag: VTag) : Diff
        data class Remove(val vTag: VTag) : Diff
    }

    private val diffs = mutableListOf<Diff>()

    fun reconcile(old: VTag, new: VTag): List<Diff> {
        diffs.clear()
        diff(old, new)
        return diffs
    }

    private fun diff(old: VTag? = null, new: VTag? = null) {
        if (old == null && new == null) {
            return
        }

        if (old == null) {
            diffs.add(Diff.Insert(new!!))
            return
        }

        if (new == null) {
            diffs.add(Diff.Remove(old))
            return
        }


        if (old.fingerprint == new.fingerprint) {
            return
        }
    }
}