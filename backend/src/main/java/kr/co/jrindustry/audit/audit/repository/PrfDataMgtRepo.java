package kr.co.jrindustry.audit.audit.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.co.jrindustry.audit.audit.entity.PrfDataMgt;

@Repository
public interface PrfDataMgtRepo extends JpaRepository<PrfDataMgt, BigInteger>, JpaSpecificationExecutor<PrfDataMgt> {
    @Query(value = """
        with RECURSIVE CategoryHierarchy AS (
            SELECT
                cat_cd,
                cat_nm,
                up_cat_cd,
                up_cat_nm,
                print_yn,
                mgt_yn,
                del_yn
            FROM
                prf_category
            WHERE
                (:catCd IS NULL OR cat_cd = :catCd)
            UNION ALL
            SELECT
                p.cat_cd,
                p.cat_nm,
                p.up_cat_cd,
                p.up_cat_nm,
                p.print_yn,
                p.mgt_yn,
                p.del_yn
            FROM
                prf_category p
            INNER JOIN
                CategoryHierarchy ch
            ON
                p.up_cat_cd = ch.cat_cd
        )
        SELECT DISTINCT
            pdm.*
        FROM
            CategoryHierarchy chc
        join
        	prf_data_mgt pdm
        on
        	chc.cat_cd = pdm.cat_cd 
        WHERE
            CASE 
                WHEN ((pdm.del_yn = 'n') AND ((:searchStd IS null) OR (pdm.in_dt > :searchStd)) AND ((:searchEd IS null) OR (pdm.in_dt < :searchEd))) THEN 
                    CASE 
                        WHEN ((:searchKey IS NULL) OR (:searchKey = 'catNm')) AND ((:searchStr IS NULL) OR (chc.cat_nm LIKE CONCAT('%', :searchStr, '%'))) THEN 1
                        WHEN ((:searchKey IS NULL) OR (:searchKey = 'catCd')) AND ((:searchStr IS NULL) OR (chc.cat_cd LIKE CONCAT('%', :searchStr, '%'))) THEN 1
                        ELSE 0
                    END
                ELSE 0
            END
        ORDER BY
            chc.cat_cd, pdm.data_mgt_seq ASC
        LIMIT :size OFFSET :page
        """, nativeQuery = true)
    List<PrfDataMgt> findByIdWithChildren(@Param("catCd") String catCd, @Param("searchKey") String searchKey, @Param("searchStr") String searchStr, @Param("page") int page, @Param("size") int size, @Param("searchStd") String searchStd, @Param("searchEd") String searchEd);
   
}