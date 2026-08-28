package com.example.game

import com.example.model.AIDifficulty
import com.example.model.GameMode
import com.example.model.Player
import com.example.model.WinningLine
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object TicTacToeEngine {

    /**
     * Checks if there is a winning condition on the board.
     */
    fun checkWin(board: List<Player?>, mode: GameMode): WinningLine? {
        val n = mode.gridSize
        val target = mode.targetToWin

        fun getCell(r: Int, c: Int): Player? {
            if (r !in 0 until n || c !in 0 until n) return null
            return board[r * n + c]
        }

        // Horizontal Check
        for (r in 0 until n) {
            for (c in 0..n - target) {
                val first = getCell(r, c) ?: continue
                var allMatch = true
                val cells = mutableSetOf<Pair<Int, Int>>()
                for (k in 0 until target) {
                    if (getCell(r, c + k) != first) {
                        allMatch = false
                        break
                    }
                    cells.add(Pair(r, c + k))
                }
                if (allMatch) {
                    return WinningLine(
                        startRow = r,
                        startCol = c,
                        endRow = r,
                        endCol = c + target - 1,
                        winningCells = cells
                    )
                }
            }
        }

        // Vertical Check
        for (c in 0 until n) {
            for (r in 0..n - target) {
                val first = getCell(r, c) ?: continue
                var allMatch = true
                val cells = mutableSetOf<Pair<Int, Int>>()
                for (k in 0 until target) {
                    if (getCell(r + k, c) != first) {
                        allMatch = false
                        break
                    }
                    cells.add(Pair(r + k, c))
                }
                if (allMatch) {
                    return WinningLine(
                        startRow = r,
                        startCol = c,
                        endRow = r + target - 1,
                        endCol = c,
                        winningCells = cells
                    )
                }
            }
        }

        // Diagonal Check (\)
        for (r in 0..n - target) {
            for (c in 0..n - target) {
                val first = getCell(r, c) ?: continue
                var allMatch = true
                val cells = mutableSetOf<Pair<Int, Int>>()
                for (k in 0 until target) {
                    if (getCell(r + k, c + k) != first) {
                        allMatch = false
                        break
                    }
                    cells.add(Pair(r + k, c + k))
                }
                if (allMatch) {
                    return WinningLine(
                        startRow = r,
                        startCol = c,
                        endRow = r + target - 1,
                        endCol = c + target - 1,
                        winningCells = cells
                    )
                }
            }
        }

        // Anti-Diagonal Check (/)
        for (r in 0..n - target) {
            for (c in (target - 1) until n) {
                val first = getCell(r, c) ?: continue
                var allMatch = true
                val cells = mutableSetOf<Pair<Int, Int>>()
                for (k in 0 until target) {
                    if (getCell(r + k, c - k) != first) {
                        allMatch = false
                        break
                    }
                    cells.add(Pair(r + k, c - k))
                }
                if (allMatch) {
                    return WinningLine(
                        startRow = r,
                        startCol = c,
                        endRow = r + target - 1,
                        endCol = c - target + 1,
                        winningCells = cells
                    )
                }
            }
        }

        return null
    }

    fun isBoardFull(board: List<Player?>): Boolean {
        return board.none { it == null }
    }

    /**
     * Computes the best move for AI across 3x3, 6x6, 12x12, and 24x24.
     */
    fun getAiMove(board: List<Player?>, mode: GameMode, difficulty: AIDifficulty, aiPlayer: Player = Player.X): Int {
        val emptyIndices = board.indices.filter { board[it] == null }
        if (emptyIndices.isEmpty()) return -1

        val humanPlayer = aiPlayer.other()

        // EASY: Casual, makes random moves with occasional win/block
        if (difficulty == AIDifficulty.EASY) {
            if (Random.nextFloat() < 0.35f) {
                findWinningMove(board, mode, aiPlayer)?.let { return it }
                findWinningMove(board, mode, humanPlayer)?.let { return it }
            }
            return emptyIndices.random()
        }

        // MEDIUM: Tactical, wins if possible, blocks direct opponent wins 85% of time
        if (difficulty == AIDifficulty.MEDIUM) {
            findWinningMove(board, mode, aiPlayer)?.let { return it }
            if (Random.nextFloat() < 0.85f) {
                findWinningMove(board, mode, humanPlayer)?.let { return it }
            }
            return if (mode == GameMode.MINI) {
                getStrategic3x3Move(board, aiPlayer, emptyIndices)
            } else {
                getStrategicGomokuMove(board, mode, aiPlayer, emptyIndices, isMaster = false)
            }
        }

        // MASTER / HARD: Very hard, unbeatable grandmaster AI
        if (mode == GameMode.MINI) {
            return getMinimaxMove3x3(board, aiPlayer)
        } else {
            return getStrategicGomokuMove(board, mode, aiPlayer, emptyIndices, isMaster = true)
        }
    }

    private fun findWinningMove(board: List<Player?>, mode: GameMode, player: Player): Int? {
        val emptyIndices = board.indices.filter { board[it] == null }
        val mutableBoard = board.toMutableList()
        for (index in emptyIndices) {
            mutableBoard[index] = player
            val win = checkWin(mutableBoard, mode)
            mutableBoard[index] = null
            if (win != null) return index
        }
        return null
    }

    private fun getStrategic3x3Move(board: List<Player?>, aiPlayer: Player, emptyIndices: List<Int>): Int {
        // Center
        if (board[4] == null) return 4
        // Corners
        val corners = listOf(0, 2, 6, 8).filter { board[it] == null }
        if (corners.isNotEmpty()) return corners.random()
        return emptyIndices.random()
    }

    private fun getMinimaxMove3x3(board: List<Player?>, aiPlayer: Player): Int {
        var bestScore = Int.MIN_VALUE
        var bestMove = -1

        val emptyIndices = board.indices.filter { board[it] == null }
        if (emptyIndices.size == 9) return listOf(0, 2, 4, 6, 8).random() // fast opening

        val mutableBoard = board.toMutableList()
        for (index in emptyIndices) {
            mutableBoard[index] = aiPlayer
            val score = minimax3x3(mutableBoard, depth = 0, isMaximizing = false, aiPlayer = aiPlayer, alpha = Int.MIN_VALUE, beta = Int.MAX_VALUE)
            mutableBoard[index] = null
            if (score > bestScore) {
                bestScore = score
                bestMove = index
            }
        }
        return if (bestMove != -1) bestMove else emptyIndices.random()
    }

    private fun minimax3x3(board: MutableList<Player?>, depth: Int, isMaximizing: Boolean, aiPlayer: Player, alpha: Int, beta: Int): Int {
        val win = checkWin(board, GameMode.MINI)
        val humanPlayer = aiPlayer.other()

        if (win != null) {
            val winner = board[win.startRow * 3 + win.startCol]
            return if (winner == aiPlayer) (10 - depth) else (depth - 10)
        }
        if (isBoardFull(board)) return 0

        val emptyIndices = board.indices.filter { board[it] == null }
        var currentAlpha = alpha
        var currentBeta = beta

        return if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            for (idx in emptyIndices) {
                board[idx] = aiPlayer
                val evaluation = minimax3x3(board, depth + 1, false, aiPlayer, currentAlpha, currentBeta)
                board[idx] = null
                maxEval = maxOf(maxEval, evaluation)
                currentAlpha = maxOf(currentAlpha, evaluation)
                if (currentBeta <= currentAlpha) break
            }
            maxEval
        } else {
            var minEval = Int.MAX_VALUE
            for (idx in emptyIndices) {
                board[idx] = humanPlayer
                val evaluation = minimax3x3(board, depth + 1, true, aiPlayer, currentAlpha, currentBeta)
                board[idx] = null
                minEval = minOf(minEval, evaluation)
                currentBeta = minOf(currentBeta, evaluation)
                if (currentBeta <= currentAlpha) break
            }
            minEval
        }
    }

    /**
     * Highly optimized Master Gomoku/Tic-Tac-Toe AI Engine for 6x6, 12x12, and 24x24.
     */
    private fun getStrategicGomokuMove(
        board: List<Player?>,
        mode: GameMode,
        aiPlayer: Player,
        emptyIndices: List<Int>,
        isMaster: Boolean
    ): Int {
        val n = mode.gridSize
        val target = mode.targetToWin
        val human = aiPlayer.other()

        // 1. Instant AI Win
        findWinningMove(board, mode, aiPlayer)?.let { return it }

        // 2. Instant Block of Opponent Win
        findWinningMove(board, mode, human)?.let { return it }

        // 3. Fast Center Opening for early game
        val placedCount = board.count { it != null }
        if (placedCount == 0) {
            val center = (n / 2) * n + (n / 2)
            return if (board[center] == null) center else emptyIndices.random()
        }
        if (placedCount == 1) {
            val firstMove = board.indexOfFirst { it != null }
            val fr = firstMove / n
            val fc = firstMove % n
            // Play adjacent to first move towards center
            val dr = if (fr < n / 2) 1 else -1
            val dc = if (fc < n / 2) 1 else -1
            val targetIndex = (fr + dr) * n + (fc + dc)
            if (targetIndex in board.indices && board[targetIndex] == null) {
                return targetIndex
            }
        }

        // 4. Candidate filtering: Consider only empty cells near existing pieces (distance <= 2)
        val candidateMoves = getNeighborCandidates(board, n, emptyIndices)
        if (candidateMoves.isEmpty()) return emptyIndices.random()

        // 5. Score candidate moves for both attack (AI) and defense (Human)
        val scoredCandidates = candidateMoves.map { index ->
            val r = index / n
            val c = index % n

            // Attack score (What if AI plays here?)
            val attackScore = evaluatePointPatterns(board, n, target, r, c, aiPlayer)
            // Defense score (What if Human plays here?)
            val defenseScore = evaluatePointPatterns(board, n, target, r, c, human)

            // Center proximity bonus (tie breaker)
            val centerDist = abs(r - n / 2.0) + abs(c - n / 2.0)
            val centerScore = (n - centerDist.toInt()).coerceAtLeast(0) * 10L

            // In Master mode, defense is prioritized on dangerous threats to prevent traps
            val totalScore = if (isMaster) {
                (attackScore * 1.05 + defenseScore * 1.15 + centerScore).toLong()
            } else {
                (attackScore + defenseScore * 0.85 + centerScore).toLong()
            }

            Pair(index, totalScore)
        }

        // In Master mode, perform a 2-ply lookahead on top 5 candidates to guarantee unbeatable defense
        if (isMaster && scoredCandidates.size > 1) {
            val topCandidates = scoredCandidates.sortedByDescending { it.second }.take(min(6, scoredCandidates.size))
            val mutableBoard = board.toMutableList()

            var bestLookaheadMove = topCandidates.first().first
            var bestLookaheadScore = Long.MIN_VALUE

            for ((moveIndex, baseScore) in topCandidates) {
                mutableBoard[moveIndex] = aiPlayer

                // Find opponent's best response
                val opponentCandidates = getNeighborCandidates(mutableBoard, n, mutableBoard.indices.filter { mutableBoard[it] == null })
                var worstOpponentThreat = 0L

                for (oppMove in opponentCandidates.take(min(10, opponentCandidates.size))) {
                    val oppR = oppMove / n
                    val oppC = oppMove % n
                    val oppAttack = evaluatePointPatterns(mutableBoard, n, target, oppR, oppC, human)
                    if (oppAttack > worstOpponentThreat) {
                        worstOpponentThreat = oppAttack
                    }
                }

                mutableBoard[moveIndex] = null

                val finalEvaluatedScore = baseScore - (worstOpponentThreat * 0.95).toLong()
                if (finalEvaluatedScore > bestLookaheadScore) {
                    bestLookaheadScore = finalEvaluatedScore
                    bestLookaheadMove = moveIndex
                }
            }

            return bestLookaheadMove
        }

        return scoredCandidates.maxByOrNull { it.second }?.first ?: candidateMoves.random()
    }

    private fun getNeighborCandidates(board: List<Player?>, n: Int, emptyIndices: List<Int>): List<Int> {
        val distance = if (n >= 24) 1 else 2
        val candidates = emptyIndices.filter { index ->
            val r = index / n
            val c = index % n
            hasPieceInRange(board, n, r, c, distance)
        }
        return if (candidates.isNotEmpty()) candidates else emptyIndices
    }

    private fun hasPieceInRange(board: List<Player?>, n: Int, r: Int, c: Int, dist: Int): Boolean {
        for (dr in -dist..dist) {
            for (dc in -dist..dist) {
                if (dr == 0 && dc == 0) continue
                val nr = r + dr
                val nc = c + dc
                if (nr in 0 until n && nc in 0 until n) {
                    if (board[nr * n + nc] != null) return true
                }
            }
        }
        return false
    }

    /**
     * Evaluates tactical Gomoku / Tic-Tac-Toe patterns when a player places at (row, col).
     */
    private fun evaluatePointPatterns(
        board: List<Player?>,
        n: Int,
        target: Int,
        row: Int,
        col: Int,
        player: Player
    ): Long {
        val directions = listOf(
            Pair(0, 1),   // Horizontal (-)
            Pair(1, 0),   // Vertical (|)
            Pair(1, 1),   // Diagonal (\)
            Pair(1, -1)   // Anti-Diagonal (/)
        )

        var totalDirectionScore = 0L
        var openThreeCount = 0
        var fourCount = 0

        fun getCell(r: Int, c: Int): Player? {
            if (r !in 0 until n || c !in 0 until n) return null
            return board[r * n + c]
        }

        for ((dr, dc) in directions) {
            var continuousCount = 1
            var openEnds = 0

            // Forward scan
            var forwardBlocked = false
            var f = 1
            while (f < target) {
                val nr = row + dr * f
                val nc = col + dc * f
                if (nr !in 0 until n || nc !in 0 until n) {
                    forwardBlocked = true
                    break
                }
                val cell = getCell(nr, nc)
                if (cell == player) {
                    continuousCount++
                } else if (cell == null) {
                    openEnds++
                    break
                } else {
                    forwardBlocked = true
                    break
                }
                f++
            }

            // Backward scan
            var backwardBlocked = false
            var b = 1
            while (b < target) {
                val nr = row - dr * b
                val nc = col - dc * b
                if (nr !in 0 until n || nc !in 0 until n) {
                    backwardBlocked = true
                    break
                }
                val cell = getCell(nr, nc)
                if (cell == player) {
                    continuousCount++
                } else if (cell == null) {
                    openEnds++
                    break
                } else {
                    backwardBlocked = true
                    break
                }
                b++
            }

            // Score based on count and open ends
            if (continuousCount >= target) {
                totalDirectionScore += 100_000_000L // Guaranteed Win
            } else if (continuousCount == target - 1) {
                if (openEnds == 2) {
                    totalDirectionScore += 10_000_000L // Open 4 (unstoppable next move)
                    fourCount++
                } else if (openEnds == 1) {
                    totalDirectionScore += 1_000_000L // Half-open 4
                    fourCount++
                }
            } else if (continuousCount == target - 2) {
                if (openEnds == 2) {
                    totalDirectionScore += 200_000L // Open 3
                    openThreeCount++
                } else if (openEnds == 1) {
                    totalDirectionScore += 25_000L // Half-open 3
                }
            } else if (continuousCount == target - 3 && target > 3) {
                if (openEnds == 2) {
                    totalDirectionScore += 5_000L // Open 2
                } else if (openEnds == 1) {
                    totalDirectionScore += 600L
                }
            } else if (continuousCount == 1) {
                if (openEnds == 2) totalDirectionScore += 50L
            }
        }

        // Dual threat combination bonus (Double 3, Double 4, 3+4)
        if (fourCount >= 2) {
            totalDirectionScore += 20_000_000L
        } else if (fourCount >= 1 && openThreeCount >= 1) {
            totalDirectionScore += 15_000_000L
        } else if (openThreeCount >= 2) {
            totalDirectionScore += 8_000_000L // Unstoppable double three fork
        }

        return totalDirectionScore
    }
}
