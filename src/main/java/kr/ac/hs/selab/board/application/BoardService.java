package kr.ac.hs.selab.board.application;

import kr.ac.hs.selab.board.domain.Board;
import kr.ac.hs.selab.board.domain.vo.Title;
import kr.ac.hs.selab.board.dto.BoardDto;
import kr.ac.hs.selab.board.infrastructure.BoardRepository;
import kr.ac.hs.selab.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    @Transactional
    public void createBoard(BoardDto dto) {
        Board newBoard = Board.of(dto);
        boardRepository.save(newBoard);
    }

    @Transactional(readOnly = true)
    public List<BoardDto> boards() {
        return boardRepository.findAll()
                .stream()
                .map(
                        board -> new BoardDto(
                                board.getTitle(),
                                board.getContent()
                        )
                ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Board board(Title title) {
        Board board = boardRepository.findByTitle(title)
                .orElseThrow(() -> new RuntimeException(""));
        return board;
    }
}
