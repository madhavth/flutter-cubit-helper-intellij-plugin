package com.madhavth.flutter_cubit_plugin.intention_action

import com.intellij.psi.PsiElement

object WrapHelper {
    @JvmStatic
    fun callExpressionFinder(psiElement: PsiElement): PsiElement? {
        var psiElementFinder = psiElement.parent
        for (i in 0..9) {
            if (psiElementFinder == null) {
                return null
            }
            if (psiElementFinder.toString() == "CALL_EXPRESSION") {
                return if (psiElementFinder.text.startsWith(psiElement.text)) {
                    psiElementFinder
                } else null
            }
            psiElementFinder = psiElementFinder.parent
        }
        return null
    }

    @JvmStatic
    fun isSelectionValid(start: Int, end: Int): Boolean {
        if (start <= -1 || end <= -1) {
            return false
        }
        return if (start >= end) {
            false
        } else true
    }
}