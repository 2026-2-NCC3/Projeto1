async function verificarFaltaAluno(pool,courseId) {

    const curso = await pool.query(
        `
        SELECT id
        FROM courses
        WHERE id = $1
        AND course_date < NOW()
        `,
        [courseId]
    );
    if(curso.rows.length === 0 ){
        throw new Error(
            'O curso ainda não foi encerrado.'
        );
    }

    const resultado = await pool.query(
        `
        SELECT
        e.user_id,
        e.id AS enrollment_id
    FROM enrollments e
    LEFT JOIN presencas p
        ON p.enrollment_id = e.id
    WHERE e.course_id = $1
    AND p.id IS NULL
        `,
        [courseId]
    );
    for (const aluno of resultado.rows){

        const faltas = await pool.query(
            `
            UPDATE users
            SET
            blacklist = TRUE,
            motivo_blacklist = '3 faltas consecutivas',
            data_blacklist  = NOW()
            WHERE id = $1
            `,
            [aluno.user_id]
        );
        const totalFaltas = faltas.rows[0].faltas;
        if(totalFaltas >= 3) {
            await pool.query(
                `
                UPDATE users
                SET 
                    blacklist = TRUE 
                    motivo_blacklist = '3 faltas conscutivas',
                    data_blacklist = NOW()
                WHERE id = $1
                `,
                [aluno.user_id]
            );
        }
    }
}
module.exports = {
    verficarFaltaAluno
};
